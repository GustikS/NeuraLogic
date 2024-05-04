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
import cz.cvut.fel.ida.logic.subsumption.SpecialBinaryPredicates;
import cz.cvut.fel.ida.logic.subsumption.SpecialVarargPredicates;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Gringo extends Grounder {

    final private ConstantFactory constantFactory;

    private String templateStr = "";

    private final Runtime runtime = Runtime.getRuntime();

    private static final Map<String, BiConsumer<Literal, StringBuilder>> specialPredicateMap = new HashMap<>();

    private static int anonymousVarCounter = 0;

    private static void specialBinaryPredicateFunction(Literal literal, StringBuilder builder, String divider) {
        builder.append(normalizeTerm(literal.arguments()[0].name(), anonymousVarCounter++))
                .append(divider)
                .append(normalizeTerm(literal.arguments()[1].name(), anonymousVarCounter++));
    }

    private static String normalizeTerm(String term, int termCount) {
        if (term.equals("_")) {
            return "AnonymousVariable" + termCount;
        }

        return term;
    }

    static {
        specialPredicateMap.put(SpecialBinaryPredicates.NEXT, (l, b) -> specialBinaryPredicateFunction(l, b, " + 1 = "));
        specialPredicateMap.put(SpecialBinaryPredicates.GT, (l, b) -> specialBinaryPredicateFunction(l, b, " > "));
        specialPredicateMap.put(SpecialBinaryPredicates.GEQ, (l, b) -> specialBinaryPredicateFunction(l, b, " >= "));
        specialPredicateMap.put(SpecialBinaryPredicates.LT, (l, b) -> specialBinaryPredicateFunction(l, b, " < "));
        specialPredicateMap.put(SpecialBinaryPredicates.LEQ, (l, b) -> specialBinaryPredicateFunction(l, b, " <= "));
        specialPredicateMap.put(SpecialBinaryPredicates.EQ, (l, b) -> specialBinaryPredicateFunction(l, b, " == "));
        specialPredicateMap.put(SpecialBinaryPredicates.NEQ, (l, b) -> specialBinaryPredicateFunction(l, b, " != "));

        specialPredicateMap.put(SpecialVarargPredicates.ALLDIFF, (l, b) -> {
            Term[] terms = l.arguments();

            if (terms.length <= 1) {
                return;
            }

            for (int i = 0; i < terms.length - 1; i++) {
                for (int j = i + 1; j < terms.length; j++) {
                    b.append(normalizeTerm(terms[i].name(), anonymousVarCounter++))
                            .append(" != ")
                            .append(normalizeTerm(terms[j].name(), anonymousVarCounter++));

                    if (i != terms.length - 2 || j != terms.length - 1) {
                        b.append(",");
                    }
                }
            }
        });
    }

    public Gringo(Settings settings) {
        super(settings);
        constantFactory = new ConstantFactory();
    }

    private void addLiteralToProgram(Literal literal, StringBuilder builder) {
        if (literal.predicate().special) {
            specialPredicateMap.get(literal.predicateName()).accept(literal, builder);
            return;
        }

        builder.append(literal.predicateName()).append("(");
        Term[] terms = literal.arguments();

        for (int i = 0; i < terms.length; i++) {
            builder.append(normalizeTerm(terms[i].name(), anonymousVarCounter++));

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
                anonymousVarCounter = 0;

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

        // No terms
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

                    int offset = bodyGrounding.size() - 1;

                    for (int j = 0; j < bodySize; j++) {
                        BodyAtom atom = body.get(j);

                        Literal literal = atom.literal;
                        if (literal.predicate().hidden) {
                            continue;
                        }

                        groundBody.add(getGroundedLiteral(literal, bodyGrounding.get(offset--)));
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

    private List<List<String>> parseGroundBody(String body) {
        final List<List<String>> parsedBody = new ArrayList<>();
        int previousIndex = 0;

        while (true) {
            int parentIndex = body.indexOf("(", previousIndex);
            int commaIndex = body.indexOf(",", previousIndex);

            if (parentIndex == -1 && commaIndex == -1) {
                if (previousIndex < body.length()) {
                    parsedBody.add(parseLiteralFromString(body.substring(previousIndex)));
                }

                return parsedBody;
            }

            if ((commaIndex < parentIndex && commaIndex != -1) || parentIndex == -1) { // handle body with predicates with no terms
                parsedBody.add(parseLiteralFromString(body.substring(previousIndex, commaIndex)));
                previousIndex = commaIndex + 1;
            } else {
                int parentCloseIndex = body.indexOf(")", parentIndex);
                parsedBody.add(parseLiteralFromString(body.substring(previousIndex, parentCloseIndex)));
                previousIndex = parentCloseIndex + 2;
            }
        }
    }

    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template) {
        timing.tic();

        final Set<ValuedFact> templateFacts = template.facts.isEmpty() ? Collections.emptySet() : new LinkedHashSet<>(template.facts);
        final Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> templateRulesAndFacts = mapToLogic(new Pair<>(template.rules, templateFacts));
        final Map<Literal, ValuedFact> allFacts = templateRulesAndFacts.s;

        if (templateStr.isEmpty()) {
            // cache the template string
            templateStr = buildProgram(templateRulesAndFacts.r.entrySet(), allFacts.keySet()).toString();
        }

        final Map<Integer, List<List<List<String>>>> groundedBodies = new HashMap<>();
        final Map<Integer, List<List<String>>> groundedHeads = new HashMap<>();

        try {
            final LinkedHashSet<ValuedFact> exampleFacts = new LinkedHashSet<>(example.flatFacts);

            if (example.conjunctions != null && !example.conjunctions.isEmpty()) {
                exampleFacts.addAll(example.conjunctions.stream().flatMap(conj -> conj.facts.stream()).collect(Collectors.toList()));
            }

            final Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> exampleRulesAndFacts = mapToLogic(new Pair<>(example.rules, exampleFacts));
            allFacts.putAll(exampleRulesAndFacts.s);

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
                    if (index == -1) {  // skip parsing facts
                        continue;
                    }

                    final int headDelimiter = line.indexOf(";");
                    final int ruleId = Integer.parseInt(line.substring(headDelimiter + 6, index - 2));

                    final String head = line.substring(1, headDelimiter);
                    final String bodyLine = line.substring(index + 2, line.length() - 1);

                    groundedBodies.computeIfAbsent(ruleId, k -> new ArrayList<>()).add(parseGroundBody(bodyLine));
                    groundedHeads.computeIfAbsent(ruleId, k -> new ArrayList<>()).add(parseLiteralFromString(head));
                }
            }

            Files.delete(temp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final GroundTemplate groundTemplate = getGroundTemplate(templateRulesAndFacts.r, groundedHeads, groundedBodies, allFacts);

        timing.toc();
        return groundTemplate;
    }

    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template, GroundTemplate memory) {
        return null;
    }

}
