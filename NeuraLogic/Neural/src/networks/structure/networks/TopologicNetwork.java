package networks.structure.networks;

import ida.utils.tuples.Pair;
import networks.structure.NeuralNetwork;
import networks.structure.WeightedNeuron;
import networks.structure.Weight;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class TopologicNetwork extends NeuralNetwork{
    private static final Logger LOG = Logger.getLogger(TopologicNetwork.class.getName());

    /**
     * All neurons combined in TOPOLOGICAL ORDERING.
     */
    @Nullable
    List<WeightedNeuron> allNeuronsTopologic;


    @Override
    public Integer getSize() {
        return allNeuronsTopologic.size();
    }

    @Override
    public <T extends WeightedNeuron> Iterator<Pair<T, Weight>> getInputs(WeightedNeuron<T> neuron) {
        return null;
    }

    @Override
    public <T extends WeightedNeuron> Iterator<Pair<T, Weight>> getOutputs(WeightedNeuron<T> neuron) {
        return null;
    }
}
