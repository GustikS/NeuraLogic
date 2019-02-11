package networks.structure.components.types;

import ida.utils.tuples.Pair;
import networks.structure.metadata.inputMappings.NeuronMapping;
import networks.structure.metadata.NetworkMetadata;
import networks.structure.metadata.inputMappings.LinkedMapping;
import networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import networks.structure.metadata.states.State;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.neurons.types.*;
import networks.structure.components.weights.Weight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DetailedNetwork<N extends State.Neural.Structure> extends TopologicNetwork<N> {
    private static final Logger LOG = Logger.getLogger(DetailedNetwork.class.getName());


    /**
     * A subset of all weights from a template that are used within this network.
     * todo NOT INDEXABLE! remove? yes remove...
     */
    @NotNull
    @Deprecated
    Weight[] activeWeights;

    /**
     * Locally valid input overloading for some neurons to facilitate dynamic structure changes
     */
    public @Nullable Map<Neuron, LinkedMapping> extraInputMapping;

    public @Nullable Map<Neuron, LinkedMapping> outputMapping;

    @Nullable
    public
    Neurons neurons;

    public DetailedNetwork() {
    }

    public class Neurons {

        public List<AtomNeuron> atomNeurons;
        public List<AggregationNeuron> aggNeurons;
        public List<RuleNeuron> ruleNeurons;
        public List<WeightedRuleNeuron> weightedRuleNeurons;
        public List<FactNeuron> factNeurons;
        public List<NegationNeuron> negationNeurons;

        List<Neuron> roots;
        List<Neuron> leaves;
    }

    Boolean recursive;

    @Nullable
    NetworkMetadata metadata;

    public Map<Neuron, LinkedMapping> calculateOutputs() {
        Map<Neuron, LinkedMapping> outputMapping = new HashMap<>();

        for (Neuron parent : allNeuronsTopologic) {
            Iterator<Neuron> inputs = getInputs(parent);
            Neuron child;
            while ((child = inputs.next()) != null) {
                LinkedMapping parentMapping = outputMapping.computeIfAbsent(child, f -> new NeuronMapping());
                parentMapping.addLink(child);
            }
        }
        return outputMapping;
    }


    public <T extends Neuron, S extends State.Neural> Pair<Iterator<T>, Iterator<Weight>> getInputs(WeightedNeuron<T, S> neuron) {
        WeightedNeuronMapping<T> inputMapping;
        if ((inputMapping = extraInputMapping != null ? (WeightedNeuronMapping<T>) extraInputMapping.get(neuron) : null) != null) {
            Iterator<T> iterator = inputMapping.iterator();
            Iterator<Weight> weightIterator = inputMapping.weightIterator();
            return new Pair<>(iterator, weightIterator);
        } else {
            return super.getInputs(neuron);
        }
    }

    public <T extends Neuron, S extends State.Neural> Iterator<T> getInputs(Neuron<T, S> neuron) {
        LinkedMapping<T> inputMapping;
        if ((inputMapping = extraInputMapping != null ? extraInputMapping.get(neuron) : null) != null) {
            return inputMapping.iterator();
        } else {
            return neuron.getInputs().iterator();
        }
    }

    public <T extends Neuron, S extends State.Neural> Iterator<T> getOutputs(Neuron<T, S> neuron) {
        LinkedMapping<T> mapping;
        if ((mapping = outputMapping != null ? outputMapping.get(neuron) : null) != null) {
            return mapping.iterator();
        } else {
            return null;
        }
    }

    public void removeInput(Neuron neuron, Pair<Neuron, Weight> input) {
        //todo to use with pruning
    }

    public boolean isRecursive() {
        return recursive;
    }

    @Override
    public Integer getSize() {
        return allNeuronsTopologic.size();
    }

}