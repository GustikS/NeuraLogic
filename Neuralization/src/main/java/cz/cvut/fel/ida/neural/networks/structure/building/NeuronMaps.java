package cz.cvut.fel.ida.neural.networks.structure.building;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundHeadRule;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundRule;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.*;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.LinkedMapping;

import java.util.*;

public class NeuronMaps {

    /**
     * Ground rules that are NOT yet in the neuronmaps
     */
    @NotNull
    public LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>>> groundRules;

    /**
     * Facts that are NOT yet in the neuronmaps
     */
    @NotNull
    public Map<Literal, ValuedFact> groundFacts;

    public Map<Literal, AtomNeurons> atomNeurons = new HashMap<>();
    public Map<GroundHeadRule, AggregationNeuron> aggNeurons = new HashMap<>();
    public Map<GroundRule, RuleNeurons> ruleNeurons = new LinkedHashMap<>();
    public Map<Literal, FactNeuron> factNeurons = new HashMap<>();
    public Set<NegationNeuron> negationNeurons = new HashSet<>();

    /**
     * Locally valid input overloading for some neurons to facilitate dynamic structure changes
     */
    @Nullable
    public Map<Neurons, LinkedMapping> extraInputMapping = new HashMap<>();

    /**
     * Does the network contain any neuron that requires pooling (i.e. does not simply propagate into all the inputs)?
     */
    public boolean containsMasking;

    /**
     * Does the network contain any neuron that combines its inputs via crossproduct scheme (i.e. requires complex gradient propagation)?
     */
    public boolean containsComplexActivations;

    public NeuronMaps(LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>>> groundRules, Map<Literal, ValuedFact> groundFacts) {
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
        sb.append("Nmaps: unused ground head literals: ").append(groundRules.size());
        sb.append(", unused groundFacts: ").append(groundFacts.size());
        sb.append(", atomNeurons: ").append(atomNeurons.size());
        sb.append(", aggNeurons: ").append(aggNeurons.size());
        sb.append(", ruleNeurons: ").append(ruleNeurons.size());
        sb.append(", factNeurons: ").append(factNeurons.size());
        sb.append(", negationNeurons: ").append(negationNeurons.size());
        return sb.toString();
    }

    private NeuronMaps() {
        this.groundRules = new LinkedHashMap<>();
        this.groundFacts = new HashMap<>();
    }

    public Object copy() {
        NeuronMaps copy = new NeuronMaps();
        copy.addAllFrom(this);
        return copy;
    }
}
