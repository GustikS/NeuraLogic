package cz.cvut.fel.ida.logic.grounding.topDown;

import com.sun.jna.Pointer;
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
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.ProgramPart;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.solving.GroundCallback;
import org.potassco.clingo.solving.Observer;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Number;
import org.potassco.clingo.symbol.Symbol;

import java.util.*;

public class Gringo extends Grounder {

    private int ruleCounter = 0;

    int count = 0;

    final private ConstantFactory constantFactory;

    final private HashMap<Integer, Integer> intToRuleId = new HashMap<>();
    final private HashMap<Integer, String> intToConst = new HashMap<>();
    final private HashMap<String, Integer> constToInt = new HashMap<>();

    final GroundingObserver observer = new GroundingObserver();

    private String templateStr = "";

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
                final int ruleMapIndex = getConstantIndex(index + "");
                intToRuleId.put(ruleMapIndex, index);

                builder.append("{");
                addLiteralToProgram(rule.getHead().literal, builder);
                builder.append(";");
                builder.append("__id(");
                builder.append(ruleMapIndex);
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

        ruleCounter = index;

        return builder;
    }

    private Literal getGroundedLiteral(Literal literal, SymbolicAtom symbolicAtom) {
        Literal copy = literal.emptyCopy();
        Function symbol = (Function) symbolicAtom.getSymbol();

        Symbol[] arguments = symbol.getArguments();
        Term[] terms = copy.arguments();

        for (int i = 0; i < terms.length; i++) {
            Number arg = (Number) arguments[i];
            terms[i] = constantFactory.construct(intToConst.get(arg.getNumber()));
        }

        return copy;
    }

    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template) {
        timing.tic();
        count++;

        Control control = new Control("--keep-facts");
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

        control.add(templateStr);

        Set<Map.Entry<HornClause, List<WeightedRule>>> ruleEntries = ruleMap.entrySet();
        Map<Literal, ValuedFact> exampleGroundFacts = mapToLogic(rulesAndFacts(example, new Template()).s);

        groundFacts.putAll(exampleGroundFacts);

        StringBuilder program = buildProgram(new HashSet<>(), exampleGroundFacts.keySet());
        control.add(program.toString());

        LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> groundRules = new LinkedHashMap<>();

        HashMap<Integer, SymbolicAtom> symbolMap = new HashMap<>();
        Map<Integer, Integer> counterToMap = new HashMap<>();

        observer.init(ruleCounter);

        final Map<Integer, List<int[]>> bodies = observer.bodies;
        final Map<Integer, List<Integer>> heads = observer.heads;

        control.registerObserver(observer);
        control.ground();

        for (SymbolicAtom symbol : control.getSymbolicAtoms()) {
            symbolMap.put(symbol.getLiteral(), symbol);
        }

        for (int key : bodies.keySet()) {
            Function fun = (Function) symbolMap.get(key).getSymbol();
            Number number = (Number) fun.getArguments()[0];
            counterToMap.put(intToRuleId.get(number.getNumber()), key);
        }

        int ruleCounter = 0;
        for (Map.Entry<HornClause, List<WeightedRule>> entry : ruleEntries) {
            for (WeightedRule rule : entry.getValue()) {
                if (!counterToMap.containsKey(ruleCounter)) {
                    ruleCounter++;
                    continue;
                }

                final int mappedRuleCounter = counterToMap.get(ruleCounter);
                final List<Integer> headGroundings = heads.get(mappedRuleCounter);
                final List<int[]> bodyGroundings = bodies.get(mappedRuleCounter);
                final int bodySize = rule.getBody().size();

                ruleCounter++;

                for (int i = 0, groundingSize = headGroundings.size(); i < groundingSize; i++) {
                    int headId = headGroundings.get(i);
                    int[] bodyGroundingIds = bodyGroundings.get(i);

                    Literal groundHead = getGroundedLiteral(rule.getHead().literal, symbolMap.get(headId));
                    List<Literal> groundBody = new ArrayList<>(bodySize);
                    List<BodyAtom> body = rule.getBody();

                    for (int j = 0; j < bodySize; j++) {
                        BodyAtom atom = body.get(j);

                        Literal literal = atom.literal;
                        if (literal.predicate().hidden) {
                            continue;
                        }

                        SymbolicAtom bodySymbol = symbolMap.get(bodyGroundingIds[bodySize - j - 1]);
                        groundBody.add(getGroundedLiteral(literal, bodySymbol));
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

        control.cleanup();
        control.close();

        GroundTemplate groundTemplate = new GroundTemplate(groundRules, groundFacts);

        timing.toc();
        return groundTemplate;
    }

    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template, GroundTemplate memory) {
        return null;
    }

    public static class GroundingObserver implements Observer {
        public Map<Integer, List<int[]>> bodies;
        public Map<Integer, List<Integer>> heads;
        public List<Integer> facts = new ArrayList<>();

        public void init(int ruleCount) {
            this.bodies = new HashMap<>(ruleCount);
            this.heads = new HashMap<>(ruleCount);
        }

        @Override
        public void rule(boolean choice, int[] head, int[] body) {
            if (body.length == 0) {
                facts.add(head[0]);
            } else {
                final int id = head[1];
                List<int[]> bodiesSub = bodies.get(id);

                if (bodiesSub == null) {
                    bodiesSub = new ArrayList<>();
                    heads.put(id, new ArrayList<>());
                    bodies.put(id, bodiesSub);
                }

                bodiesSub.add(body);
                heads.get(id).add(head[0]);
            }
        }
    }
}
