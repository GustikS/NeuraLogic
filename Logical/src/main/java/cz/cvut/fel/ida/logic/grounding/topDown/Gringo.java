package cz.cvut.fel.ida.logic.grounding.topDown;

import cz.cvut.fel.ida.logic.*;
import cz.cvut.fel.ida.logic.constructs.building.factories.ConstantFactory;
import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.BodyAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundHeadRule;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundRule;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.grounding.GroundTemplate;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.grounding.Grounder;
import cz.cvut.fel.ida.logic.grounding.constructs.GroundRulesCollection;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Gringo extends Grounder {

    final private ConstantFactory constantFactory;
    final private HashMap<Integer, String> intToConst = new HashMap<>();
    final private HashMap<String, Integer> constToInt = new HashMap<>();

    private String templateStr = "";

    private final Runtime runtime = Runtime.getRuntime();

    public Gringo(Settings settings) {
        super(settings);
        constantFactory = new ConstantFactory();
    }

    private int getConstantIndex(String value) {
        int index = constToInt.getOrDefault(value, -1);
        if (index == -1) {
            index = constToInt.size();
            constToInt.put(value, index);
            intToConst.put(index, value);
        }

        return index;
    }

    private void addLiteralToProgram(Literal literal, StringBuilder builder) {
        builder.append(literal.predicateName());
        builder.append("(");
        Term[] terms = literal.arguments();

        for (int i = 0; i < terms.length; i++) {
            if (terms[i] instanceof Variable) {
                builder.append(terms[i].name());
            } else {
                int index = getConstantIndex(terms[i].name());
                builder.append(index);
            }

            if (i != terms.length - 1) {
                builder.append(",");
            }
        }

        builder.append(")");
    }

    private StringBuilder buildProgram(Set<Map.Entry<HornClause, List<WeightedRule>>> ruleEntries, Set<Literal> facts) {
        StringBuilder builder = new StringBuilder();

        for (Literal fact : facts) {
            builder.append("{");
            addLiteralToProgram(fact, builder);
            builder.append("}.");
        }

        int index = 0;

        for (Map.Entry<HornClause, List<WeightedRule>> entry : ruleEntries) {
            for (WeightedRule rule : entry.getValue()) {
                builder.append("{");
                addLiteralToProgram(rule.getHead().literal, builder);
                builder.append(";__id(");
                builder.append(index);
                builder.append(")}:-");

                index++;

                List<BodyAtom> bodyAtoms = rule.getBody();
                for (int i = 0, size = bodyAtoms.size() - 1; i < size; i++) {
                    addLiteralToProgram(bodyAtoms.get(i).literal, builder);
                    builder.append(",");
                }
                addLiteralToProgram(bodyAtoms.get(bodyAtoms.size() - 1).literal, builder);
                builder.append(".");
            }
        }

        return builder;
    }

    private Literal getGroundedLiteral(Literal literal, List<String> parsedLiteral) {
        Literal copy = literal.emptyCopy();
        Term[] terms = copy.arguments();

        for (int i = 0; i < terms.length; i++) {
            String term = intToConst.get(Integer.parseInt(parsedLiteral.get(i)));
            terms[i] = constantFactory.construct(term);
        }

        return copy;
    }

    private List<String> parseLiteralFromString(String literal) {
        final int termIndex = literal.indexOf("(");
        final int len = literal.length();

        if (termIndex > len - 2) {
            return Collections.EMPTY_LIST;
        }

        int previousIndex = termIndex;
        int commaIndex = -1;

        List<String> result = new ArrayList<>();
        while ((commaIndex = literal.indexOf(",", previousIndex + 1)) != -1) {
            result.add(literal.substring(previousIndex + 1, commaIndex));
            previousIndex = commaIndex;
        }

        if (literal.charAt(len - 1) == ')') {
            result.add(literal.substring(previousIndex + 1, len - 1));
        } else {
            result.add(literal.substring(previousIndex + 1, len));
        }

        return result;
    }

    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template) {
        timing.tic();

        Map<HornClause, List<WeightedRule>> ruleMap = template.hornClauses;
        Map<Literal, ValuedFact> groundFacts = null;

        if (ruleMap == null) {
            Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> rulesAndFacts = mapToLogic(rulesAndFacts(new LiftedExample(), template));
            ruleMap = rulesAndFacts.r;
            groundFacts = rulesAndFacts.s;

            template.hornClauses = ruleMap;
            Set<Literal> facts = groundFacts.keySet();
            StringBuilder program = buildProgram(ruleMap.entrySet(), facts);
            templateStr = program.toString();
        } else {
            groundFacts = mapToLogic(rulesAndFacts(new LiftedExample(), template)).s;
        }

        Set<Map.Entry<HornClause, List<WeightedRule>>> ruleEntries = ruleMap.entrySet();
        LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> groundRules = new LinkedHashMap<>();
        Map<Integer, List<List<List<String>>>> bodies = new HashMap<>();
        Map<Integer, List<List<String>>> heads = new HashMap<>();

        try {
            Path temp = Files.createTempFile("", ".lp");

            try (FileWriter fw = new FileWriter(temp.toFile()); BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(templateStr);
                Map<Literal, ValuedFact> exampleGroundFacts = mapToLogic(rulesAndFacts(example, new Template()).s);
                groundFacts.putAll(exampleGroundFacts);
                bw.append(buildProgram(new HashSet<>(), exampleGroundFacts.keySet()));
            }

            Process process = runtime.exec("gringo " + temp + " --text");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int index = line.indexOf(":-");
                    if (index == -1) {
                        continue;
                    }

                    int headDelimiter = line.indexOf(";");
                    int ruleId = Integer.parseInt(line.substring(headDelimiter + 6, index - 2));
                    String head = line.substring(1, headDelimiter);

                    String[] body = line.substring(index + 2, line.length() - 1).split("\\),");
                    List<String> parsedHead = parseLiteralFromString(head);
                    List<List<String>> parsedBody = new ArrayList<>(body.length);

                    for (int i = 0; i < body.length; i++) {
                        parsedBody.add(parseLiteralFromString(body[i]));
                    }

                    bodies.computeIfAbsent(ruleId, k -> new ArrayList<>()).add(parsedBody);
                    heads.computeIfAbsent(ruleId, k -> new ArrayList<>()).add(parsedHead);
                }
            }

            Files.delete(temp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int ruleCounter = 0;
        for (Map.Entry<HornClause, List<WeightedRule>> entry : ruleEntries) {
            for (WeightedRule rule : entry.getValue()) {
                if (!bodies.containsKey(ruleCounter)) {
                    ruleCounter++;
                    continue;
                }

                final List<List<String>> headGroundings = heads.get(ruleCounter);
                final List<List<List<String>>> bodyGroundings = bodies.get(ruleCounter);
                final int bodySize = rule.getBody().size();

                ruleCounter++;

                for (int i = 0, groundingSize = headGroundings.size(); i < groundingSize; i++) {
                    List<String> headGrouding = headGroundings.get(i);
                    List<List<String>> bodyGrounding = bodyGroundings.get(i);

                    Literal groundHead = getGroundedLiteral(rule.getHead().literal, headGrouding);
                    List<Literal> groundBody = new ArrayList<>(bodySize);
                    List<BodyAtom> body = rule.getBody();

                    for (int j = 0; j < bodySize; j++) {
                        BodyAtom atom = body.get(j);

                        Literal literal = atom.literal;
                        if (literal.predicate().hidden) {
                            continue;
                        }

                        groundBody.add(getGroundedLiteral(literal, bodyGrounding.get(bodySize - j - 1)));
                    }

                    GroundRule grounding = new GroundRule(rule, groundHead, groundBody.toArray(new Literal[groundBody.size()]));
                    Map<GroundHeadRule, Collection<GroundRule>> rules2groundings = groundRules.computeIfAbsent(grounding.groundHead, k -> new LinkedHashMap<>()); //we still want unique rules at least

                    //aggregation neurons correspond to lifted rule with particular ground head
                    GroundHeadRule groundHeadRule = rule.groundHeadRule(grounding.groundHead);

                    Collection<GroundRule> ruleGroundings = rules2groundings.computeIfAbsent(groundHeadRule, k -> GroundRulesCollection.getGroundingCollection(rule));    //here we choose whether we want only unique ground bodies or not
                    ruleGroundings.add(grounding);

                }
            }
        }

        GroundTemplate groundTemplate = new GroundTemplate(groundRules, groundFacts);

        timing.toc();
        return groundTemplate;
    }

    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template, GroundTemplate memory) {
        return null;
    }

}
