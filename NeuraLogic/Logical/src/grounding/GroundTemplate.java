package grounding;

import constructs.example.LiftedExample;
import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.BodyAtom;
import constructs.template.Template;
import constructs.template.WeightedRule;
import constructs.template.templates.GraphTemplate;
import constructs.template.transforming.TemplateReducing;
import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.subsumption.Matching;
import learning.Example;
import networks.structure.lrnnTypes.AggregationNeuron;
import networks.structure.lrnnTypes.AtomNeuron;
import networks.structure.lrnnTypes.FactNeuron;
import networks.structure.lrnnTypes.RuleNeuron;

import java.util.*;

/**
 * Temporary structure created during grounding for transfer between ground Herbrand model (rules+facts) and neural network.
 */
public class GroundTemplate extends GraphTemplate implements Example {
    String id;

    public static class Neurons {
        Map<Literal, AtomNeuron> atomNeurons = new HashMap<>();
        Map<WeightedRule, AggregationNeuron> aggNeurons = new HashMap<>();
        Map<WeightedRule, RuleNeuron> ruleNeurons = new LinkedHashMap<>();
        Map<Literal, FactNeuron> factNeurons = new HashMap<>();
    }

    /**
     * Temp (for current pair of Template+Example) structure (head -> rules -> ground bodies) for traversing the graph of groundings
     */
    public LinkedHashMap<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> groundRules;

    /**
     * Temp (for current pair of Template+Example) set of true ground facts
     */
    public Map<Literal, ValuedFact> groundFacts;

    public GroundTemplate(LinkedHashMap<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> groundRules, Map<Literal, ValuedFact> groundFacts) {
        this.groundRules = groundRules;
        this.groundFacts = groundFacts;
    }

    public GroundTemplate(GroundTemplate other) {
        this.groundRules = other.groundRules;
        this.groundFacts = other.groundFacts;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getSize() {
        return groundRules.size();
    }

    public GroundTemplate prune(QueryAtom queryAtom) {
        GroundTemplate groundTemplate = new GroundTemplate(this);
        LinkedHashMap<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> support = new LinkedHashMap<>();
        Matching matching = new Matching();
        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> entry : groundRules.entrySet()) {

            if (queryAtom.headAtom.literal.predicate().equals(entry.getKey().predicate()) && matching.subsumption(new Clause(queryAtom.headAtom.literal), new Clause(entry.getKey()))) { //todo check this method
                LinkedHashMap<WeightedRule, List<WeightedRule>> ruleMap = support.computeIfAbsent(entry.getKey(), f -> new LinkedHashMap<WeightedRule, List<WeightedRule>>());

                for (Map.Entry<WeightedRule, List<WeightedRule>> groundings : entry.getValue().entrySet()) {
                    List<WeightedRule> weightedRules = ruleMap.computeIfAbsent(groundings.getKey(), f -> new ArrayList<>());

                    for (WeightedRule grounding : groundings.getValue()) {
                        weightedRules.add(grounding);
                        recursePrune(grounding, support, new HashSet<>());
                    }
                }
            }
        }
        groundTemplate.groundRules = support;
        return this;
    }

    private void recursePrune(WeightedRule grounding, LinkedHashMap<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> support, Set<Literal> closedList) {
        for (BodyAtom bodyAtom : grounding.body) {
            if (closedList.contains(bodyAtom.literal)) {
                continue;
            }
            closedList.add(bodyAtom.literal);

            LinkedHashMap<WeightedRule, List<WeightedRule>> validRules = groundRules.get(bodyAtom.literal);
            LinkedHashMap<WeightedRule, List<WeightedRule>> nextRules = support.computeIfAbsent(bodyAtom.literal, f -> new LinkedHashMap<WeightedRule, List<WeightedRule>>());

            for (Map.Entry<WeightedRule, List<WeightedRule>> validGroundings : validRules.entrySet()) {
                List<WeightedRule> weightedRules = nextRules.computeIfAbsent(validGroundings.getKey(), f -> new ArrayList<>());

                for (WeightedRule nextGrounding : validGroundings.getValue()) {
                    weightedRules.add(nextGrounding);
                    recursePrune(nextGrounding, support, closedList);
                }
            }
        }
    }

}
