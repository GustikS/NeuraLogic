package networks.structure.networks;

import ida.utils.tuples.Pair;
import networks.evaluation.iteration.State;
import networks.structure.neurons.Neuron;
import networks.structure.neurons.WeightedNeuron;
import networks.structure.weights.Weight;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * A class with an explicitly stored (topological) ordering of neurons.
 */
public class TopologicNetwork<N extends State> extends NeuralNetwork<N> {
    private static final Logger LOG = Logger.getLogger(TopologicNetwork.class.getName());

    /**
     * All neurons combined in TOPOLOGICAL ORDERING.
     */
    @Nullable
    List<Neuron> allNeuronsTopologic;

    @Override
    public Integer getSize() {
        return allNeuronsTopologic.size();
    }

    @Override
    public <T extends WeightedNeuron, S extends State> Iterator<Pair<T, Weight>> getInputs(WeightedNeuron<T, S> neuron) {
        return null;
    }

    @Override
    public <T extends Neuron, S extends State> Iterator<T> getInputs(Neuron<T, S> neuron) {
        return null;
    }

    @Override
    public <T extends Neuron, S extends State> Iterator<T> getOutputs(Neuron<T, S> neuron) {
        return null;
    }
}
