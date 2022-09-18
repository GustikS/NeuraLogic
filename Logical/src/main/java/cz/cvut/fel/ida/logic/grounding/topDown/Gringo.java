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
import java.util.stream.Collectors;

public class Gringo extends Grounder {

    final private ConstantFactory constantFactory;

    private String templateStr = "";

    private final Runtime runtime = Runtime.getRuntime();

    public Gringo(Settings settings) {
        super(settings);
        constantFactory = new ConstantFactory();
    }

    private void addLiteralToProgram(Literal literal, StringBuilder builder) {
        builder.append(literal.predicateName()).append("(");
        Term[] terms = literal.arguments();

        for (int i = 0; i < terms.length; i++) {
            builder.append(terms[i].name());

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
            String term = parsedLiteral.get(i);
            terms[i] = constantFactory.construct(term);
        }

        return copy;
    }

    private List<String> parseLiteralFromString(String literal) {
        final int termIndex = literal.indexOf("(");
        final int len = literal.length();

        if (termIndex > len - 2) {
            return Collections.emptyList();
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

    private GroundTemplate getGroundTemplate(
            Map<HornClause, List<WeightedRule>> ruleMap,
            Map<Integer, List<List<String>>> groundedHeads,
            Map<Integer, List<List<List<String>>>> groundedBodies,
            Map<Literal, ValuedFact> groundFacts
    ) {
        Set<Map.Entry<HornClause, List<WeightedRule>>> ruleEntries = ruleMap.entrySet();
        LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> groundRules = new LinkedHashMap<>();

        int ruleCounter = 0;
        for (Map.Entry<HornClause, List<WeightedRule>> entry : ruleEntries) {
            for (WeightedRule rule : entry.getValue()) {
                if (!groundedBodies.containsKey(ruleCounter)) {
                    ruleCounter++;
                    continue;
                }

                final List<List<String>> headGroundings = groundedHeads.get(ruleCounter);
                final List<List<List<String>>> bodyGroundings = groundedBodies.get(ruleCounter);
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
                    Map<GroundHeadRule, Collection<GroundRule>> rules2groundings = groundRules.computeIfAbsent(
                            grounding.groundHead, k -> new LinkedHashMap<>()
                    ); //we still want unique rules at least

                    //aggregation neurons correspond to lifted rule with particular ground head
                    GroundHeadRule groundHeadRule = rule.groundHeadRule(grounding.groundHead);

                    Collection<GroundRule> ruleGroundings = rules2groundings.computeIfAbsent(groundHeadRule, k -> GroundRulesCollection.getGroundingCollection(rule));    //here we choose whether we want only unique ground bodies or not
                    ruleGroundings.add(grounding);

                }
            }
        }

        return new GroundTemplate(groundRules, groundFacts);
    }

    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template) {
        timing.tic();

        Map<HornClause, List<WeightedRule>> ruleMap = template.hornClauses;

        final Set<ValuedFact> flatFacts = template.facts.isEmpty() ? Collections.emptySet() : new LinkedHashSet<>(template.facts);
        final Set<WeightedRule> rules = template.rules;
        final Pair<Set<WeightedRule>, Set<ValuedFact>> templateRulesAndFacts = new Pair<>(rules, flatFacts);

        final Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> rulesAndFacts = mapToLogic(templateRulesAndFacts);
        final Map<Literal, ValuedFact> groundFacts = rulesAndFacts.s;

        if (ruleMap == null) {
            ruleMap = rulesAndFacts.r;
            template.hornClauses = ruleMap;
            templateStr = buildProgram(ruleMap.entrySet(), groundFacts.keySet()).toString();
        }

        final Map<Integer, List<List<List<String>>>> groundedBodies = new HashMap<>();
        final Map<Integer, List<List<String>>> groundedHeads = new HashMap<>();

        try {
            final LinkedHashSet<ValuedFact> exampleFacts = new LinkedHashSet<>(example.flatFacts);

            if (example.conjunctions != null && !example.conjunctions.isEmpty()) {
                exampleFacts.addAll(example.conjunctions.stream().flatMap(conj -> conj.facts.stream()).collect(Collectors.toList()));
            }

            final Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> exampleRulesAndFacts = mapToLogic(new Pair<>(example.rules, exampleFacts));
            groundFacts.putAll(exampleRulesAndFacts.s);

            final Path temp = Files.createTempFile("", ".lp");

            try (FileWriter fw = new FileWriter(temp.toFile()); BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(templateStr);
                bw.append(buildProgram(exampleRulesAndFacts.r.entrySet(), exampleRulesAndFacts.s.keySet()));
            }

            Process process = runtime.exec("gringo " + temp + " --text");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final int index = line.indexOf(":-");
                    if (index == -1) {
                        continue;
                    }

                    final int headDelimiter = line.indexOf(";");
                    final int ruleId = Integer.parseInt(line.substring(headDelimiter + 6, index - 2));
                    final String head = line.substring(1, headDelimiter);

                    final String[] body = line.substring(index + 2, line.length() - 1).split("\\),");
                    final List<String> parsedHead = parseLiteralFromString(head);
                    final List<List<String>> parsedBody = new ArrayList<>(body.length);

                    for (String s : body) {
                        parsedBody.add(parseLiteralFromString(s));
                    }

                    groundedBodies.computeIfAbsent(ruleId, k -> new ArrayList<>()).add(parsedBody);
                    groundedHeads.computeIfAbsent(ruleId, k -> new ArrayList<>()).add(parsedHead);
                }
            }

            Files.delete(temp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final GroundTemplate groundTemplate = getGroundTemplate(ruleMap, groundedHeads, groundedBodies, groundFacts);

        timing.toc();
        return groundTemplate;
    }

    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template, GroundTemplate memory) {
        return null;
    }

}
