package networks.structure.networks;

import ida.utils.tuples.Pair;
import networks.structure.NeuralNetwork;
import networks.structure.Neuron;
import networks.structure.Weight;
import networks.structure.WeightedNeuron;
import networks.structure.metadata.LinkedNeuronMapping;
import networks.structure.metadata.NetworkMetadata;
import networks.structure.metadata.NeuronMapping;
import networks.structure.metadata.WeightedNeuronMapping;
import networks.structure.neurons.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public class DetailedNetwork extends NeuralNetwork {
    private static final Logger LOG = Logger.getLogger(DetailedNetwork.class.getName());

    /**
     * Locally valid input overloading for some neurons to facilitate dynamic structure changes
     */
    public @Nullable Map<Neuron, NeuronMapping> extraInputMapping;

    public @Nullable Map<Neuron, NeuronMapping> outputMapping;

    List<Neuron> allNeuronsTolopogic;

    @Nullable
    Neurons neurons;

    public class Neurons {

        List<AtomNeuron> atomNeurons;
        List<AggregationNeuron> aggNeurons;
        List<RuleNeuron> ruleNeurons;
        List<FactNeuron> factNeurons;
        List<NegationNeuron> negationNeurons;

        List<Neuron> roots;
        List<Neuron> leaves;
    }

    Boolean recursive;

    @Nullable
    NetworkMetadata metadata;

    public DetailedNetwork(Collection<AtomNeuron> atomNeurons, Collection<AggregationNeuron> aggregationNeurons, Collection<RuleNeurons> ruleNeurons, Collection<FactNeuron> factNeurons, Set<NegationNeuron> negationNeurons) {
        this.neurons = new Neurons();
        this.neurons.atomNeurons = new ArrayList<>(atomNeurons);
        this.neurons.aggNeurons = new ArrayList<>(aggregationNeurons);
        this.neurons.ruleNeurons = new ArrayList<>(ruleNeurons);
        this.neurons.factNeurons = new ArrayList<>(factNeurons);
        this.neurons.negationNeurons = new ArrayList<>(negationNeurons);

        this.allNeuronsTolopogic = new ArrayList<>(atomNeurons.size() + aggregationNeurons.size() + ruleNeurons.size() + factNeurons.size() + negationNeurons.size());
        this.allNeuronsTolopogic.addAll(atomNeurons);
        this.allNeuronsTolopogic.addAll(aggregationNeurons);
        this.allNeuronsTolopogic.addAll(ruleNeurons);
        this.allNeuronsTolopogic.addAll(factNeurons);
        this.allNeuronsTolopogic.addAll(negationNeurons);

        this.allNeuronsTolopogic = topologicSort(this.allNeuronsTolopogic);

        this.outputMapping = calculateOutputs();
    }

    public Map<Neuron, NeuronMapping> calculateOutputs() {
        Map<Neuron, NeuronMapping> outputMapping = new HashMap<>();
        for (Neuron parent : allNeuronsTolopogic) {
            Iterator<Neuron> inputs = getInputs(parent);
            Neuron child;
            while ((child = inputs.next()) != null) {
                NeuronMapping parentMapping = outputMapping.computeIfAbsent(child, f -> new LinkedNeuronMapping());
                parentMapping.addLink(child);
            }
        }
        return outputMapping;
    }

    public <T extends WeightedNeuron> Iterator<Pair<T, Weight>> getInputs(WeightedNeuron<T> neuron) {
        WeightedNeuronMapping<T> inputMapping;
        if ((inputMapping = extraInputMapping != null ? (WeightedNeuronMapping<T>) extraInputMapping.get(neuron) : null) != null) {
            return inputMapping.iterator();
        } else {
            return neuron.getWeightedInputs();
        }
    }

    public <T extends Neuron> Iterator<T> getInputs(Neuron<T> neuron) {
        NeuronMapping<T> inputMapping;
        if ((inputMapping = extraInputMapping != null ? extraInputMapping.get(neuron) : null) != null) {
            return inputMapping.iterator();
        } else {
            return neuron.getInputs().iterator();
        }
    }

    public void removeInput(Neuron neuron, Pair<Neuron, Weight> input){
        //todo to use with pruning
    }

    public <T extends Neuron> Iterator<T> getOutputs(Neuron<T> neuron) {
        NeuronMapping<T> mapping;
        if ((mapping = outputMapping != null ? outputMapping.get(neuron) : null) != null) {
            return mapping.iterator();
        } else {
            return null;
        }
    }

    public boolean isRecursive() {
        return recursive;
    }

    @Override
    public Integer getSize() {
        return allNeuronsTolopogic.size();
    }

    public List<Neuron> topologicSort(List<Neuron> allNeurons) {
        Set<Neuron> visited = new HashSet<>();
        Stack<Neuron> stack = new Stack<>();

        for (Neuron neuron : allNeurons) {
            if (!visited.contains(neuron))
                topoSortRecursive(neuron, visited, stack);
        }

        List<Neuron> neurons = new ArrayList<>(allNeurons.size());
        while (!stack.empty())
            neurons.add(stack.pop());

        return neurons;
    }

    private void topoSortRecursive(Neuron neuron, Set<Neuron> visited, Stack<Neuron> stack) {
        visited.add(neuron);

        Iterator<Pair<Neuron, Weight>> inputs = getInputs(neuron);
        while (inputs.hasNext()) {
            Pair<Neuron, Weight> next = inputs.next();
            if (!visited.contains(next.r)) {
                topoSortRecursive(next.r, visited, stack);
            }
        }
        stack.push(neuron);
    }
}