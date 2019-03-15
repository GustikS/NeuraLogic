package networks.structure.building;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import constructs.example.ValuedFact;
import constructs.template.components.WeightedRule;
import ida.ilp.logic.Literal;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.types.*;
import networks.structure.metadata.inputMappings.LinkedMapping;

import java.util.*;

public class NeuronMaps {

    /**
     * Ground rules that are NOT yet in the neuronmaps
     */
    @NotNull
    public LinkedHashMap<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> groundRules;    //todo next

    /**
     * Facts that are NOT yet in the neuronmaps
     */
    @NotNull
    public Map<Literal, ValuedFact> groundFacts;

    public Map<Literal, AtomNeuron> atomNeurons = new HashMap<>();
    public Map<WeightedRule, AggregationNeuron> aggNeurons = new HashMap<>();
    public Map<WeightedRule, RuleNeurons> ruleNeurons = new LinkedHashMap<>();
    public Map<Literal, FactNeuron> factNeurons = new HashMap<>();
    public Set<NegationNeuron> negationNeurons = new HashSet<>();

    /**
     * Locally valid input overloading for some neurons to facilitate dynamic structure changes
     */
    @Nullable
    public Map<BaseNeuron, LinkedMapping> extraInputMapping = new HashMap<>();

    public NeuronMaps(LinkedHashMap<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> groundRules, Map<Literal, ValuedFact> groundFacts) {
        this.groundRules = new LinkedHashMap<>(groundRules);
        this.groundFacts = new HashMap<>(groundFacts);
    }

    public void addAllFrom(NeuronMaps neuronMaps) {
        atomNeurons.putAll(neuronMaps.atomNeurons);
        aggNeurons.putAll(neuronMaps.aggNeurons);
        ruleNeurons.putAll(neuronMaps.ruleNeurons);
        factNeurons.putAll(neuronMaps.factNeurons);

        extraInputMapping.putAll(neuronMaps.extraInputMapping);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nmaps: groundRules: ").append(groundRules.size());
        sb.append(", groundFacts: ").append(groundFacts.size());
        sb.append(", atomNeurons: ").append(atomNeurons.size());
        sb.append(", aggNeurons: ").append(aggNeurons.size());
        sb.append(", ruleNeurons: ").append(ruleNeurons.size());
        sb.append(", factNeurons: ").append(factNeurons.size());
        sb.append(", negationNeurons: ").append(negationNeurons.size());
        return sb.toString();
    }
}
