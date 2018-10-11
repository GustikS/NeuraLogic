package grounding;

import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.BodyAtom;
import constructs.template.WeightedRule;
import constructs.template.templates.GraphTemplate;
import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.subsumption.Matching;
import learning.Example;
import networks.structure.metadata.NeuronMapping;
import networks.structure.networks.DetailedNetwork;
import networks.structure.neurons.Neuron;
import networks.structure.neurons.creation.AggregationNeuron;
import networks.structure.neurons.creation.AtomNeuron;
import networks.structure.neurons.creation.FactNeuron;
import networks.structure.neurons.creation.RuleNeurons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Temporary structure created during grounding for transfer between ground Herbrand model (rules+facts) and neural network.
 */
public class GroundTemplate extends GraphTemplate implements Example {
    String id;

    /**
     * Temp (for current pair of Template+Example) structure (head -> rules -> ground bodies) for traversing the graph of groundings
     */
    @NotNull
    public LinkedHashMap<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> groundRules;    //todo optimize access by further aggregating literals with the same predicate for subsumption testing?

    /**
     * Temp (for current pair of Template+Example) set of true ground facts
     */
    @NotNull
    public Map<Literal, ValuedFact> groundFacts;

    public NeuronMaps neuronMaps;

    public static class NeuronMaps {
        public Map<Literal, AtomNeuron> atomNeurons = new HashMap<>();
        public Map<WeightedRule, AggregationNeuron> aggNeurons = new HashMap<>();
        public Map<WeightedRule, RuleNeurons> ruleNeurons = new LinkedHashMap<>();
        public Map<Literal, FactNeuron> factNeurons = new HashMap<>();

        /**
         * Locally valid input overloading for some neurons to facilitate dynamic structure changes
         */
        @Nullable
        public Map<Neuron, NeuronMapping> extraInputMapping = new HashMap<>();

        public void addAllFrom(NeuronMaps neuronMaps) {
            atomNeurons.putAll(neuronMaps.atomNeurons);
            aggNeurons.putAll(neuronMaps.aggNeurons);
            ruleNeurons.putAll(neuronMaps.ruleNeurons);
            factNeurons.putAll(neuronMaps.factNeurons);

            extraInputMapping.putAll(neuronMaps.extraInputMapping);
        }
    }

    @Nullable
    DetailedNetwork neuralNetwork;


    public GroundTemplate() {

    }

    public GroundTemplate(LinkedHashMap<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> groundRules, Map<Literal, ValuedFact> groundFacts) {
        this.groundRules = groundRules;
        this.groundFacts = groundFacts;
        this.neuronMaps = new NeuronMaps();
    }

    public GroundTemplate(GroundTemplate other) {
        this.groundRules = other.groundRules;
        this.groundFacts = other.groundFacts;
        this.neuronMaps = other.neuronMaps;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getSize() {
        return groundRules.size();
    }


    /**
     * Returns set difference of this GroundTemplate w.r.t. memory in terms of Rules and Facts,
     * but takes all the previous neurons from memory.
     * @param memory
     * @return
     */
    public GroundTemplate diffAgainst(GroundTemplate memory) {
        GroundTemplate diff = new GroundTemplate();

        //1) copy all ground rules into new diff
        diff.groundRules = new LinkedHashMap<>();
        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> entry : this.groundRules.entrySet()) {
            LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>> put = diff.groundRules.put(entry.getKey(), new LinkedHashMap<>());
            for (Map.Entry<WeightedRule, LinkedHashSet<WeightedRule>> entry2 : entry.getValue().entrySet()) {
                LinkedHashSet<WeightedRule> put1 = put.put(entry2.getKey(), new LinkedHashSet<>());
                put1.addAll(entry2.getValue());
            }
        }
        //2) copy all ground facts into new diff
        diff.groundFacts = new HashMap<>();
        diff.groundFacts.putAll(this.groundFacts);

        //forget repetitive ground rules
        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> entry : memory.groundRules.entrySet()) {
            for (Map.Entry<WeightedRule, LinkedHashSet<WeightedRule>> entry2 : entry.getValue().entrySet()) {
                for (WeightedRule rule : entry2.getValue()) {
                    //delete pointers to the newly proved rules which are equivalent the the previously proved rules
                    LinkedHashSet<WeightedRule> rules = diff.groundRules.get(entry.getKey()).get(entry2.getKey());
                    rules.remove(rule); //todo change to factory method which tells if new instead and go back to arraylist instead of LinkedHashSet for the groundings??(will be faster?)
                }
            }
        }
        //also forget newly proved equivalent facts
        diff.groundFacts.keySet().removeAll(memory.groundFacts.keySet());

        //but take all the previously created neurons
        diff.neuronMaps.addAllFrom(memory.neuronMaps);

        return diff;
    }

    public GroundTemplate prune(QueryAtom queryAtom) {
        GroundTemplate groundTemplate = new GroundTemplate(this);
        LinkedHashMap<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> support = new LinkedHashMap<>();
        Matching matching = new Matching();
        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> entry : groundRules.entrySet()) {

            if (queryAtom.headAtom.literal.predicate().equals(entry.getKey().predicate()) && matching.subsumption(new Clause(queryAtom.headAtom.literal), new Clause(entry.getKey()))) { //todo check this method
                LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>> ruleMap = support.computeIfAbsent(entry.getKey(), f -> new LinkedHashMap<>());

                for (Map.Entry<WeightedRule, LinkedHashSet<WeightedRule>> groundings : entry.getValue().entrySet()) {
                    LinkedHashSet<WeightedRule> weightedRules = ruleMap.computeIfAbsent(groundings.getKey(), f -> new LinkedHashSet<>());

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

    private void recursePrune(WeightedRule grounding, LinkedHashMap<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> support, Set<Literal> closedList) {
        for (BodyAtom bodyAtom : grounding.body) {
            if (closedList.contains(bodyAtom.literal)) {
                continue;
            }
            closedList.add(bodyAtom.literal);

            LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>> validRules = groundRules.get(bodyAtom.literal);
            LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>> nextRules = support.computeIfAbsent(bodyAtom.literal, f -> new LinkedHashMap<>());

            for (Map.Entry<WeightedRule, LinkedHashSet<WeightedRule>> validGroundings : validRules.entrySet()) {
                LinkedHashSet<WeightedRule> weightedRules = nextRules.computeIfAbsent(validGroundings.getKey(), f -> new LinkedHashSet<>());

                for (WeightedRule nextGrounding : validGroundings.getValue()) {
                    weightedRules.add(nextGrounding);
                    recursePrune(nextGrounding, support, closedList);
                }
            }
        }
    }

}
