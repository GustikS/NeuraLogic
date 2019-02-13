package networks.structure.building;

import constructs.template.components.WeightedRule;
import ida.ilp.logic.Literal;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.types.*;
import networks.structure.metadata.inputMappings.LinkedMapping;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NeuronMaps {

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

    public void addAllFrom(NeuronMaps neuronMaps) {
        atomNeurons.putAll(neuronMaps.atomNeurons);
        aggNeurons.putAll(neuronMaps.aggNeurons);
        ruleNeurons.putAll(neuronMaps.ruleNeurons);
        factNeurons.putAll(neuronMaps.factNeurons);

        extraInputMapping.putAll(neuronMaps.extraInputMapping);
    }
}
