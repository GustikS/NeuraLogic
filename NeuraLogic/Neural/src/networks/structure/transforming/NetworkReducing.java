package networks.structure.transforming;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.types.AtomNeuron;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.*;

/**
 * Created by gusta on 9.3.17.
 */
public interface NetworkReducing {

    public abstract NeuralNetwork reduce(DetailedNetwork<State.Neural.Structure> inet, AtomNeuron<State.Neural> outputStart);

    public static NetworkReducing getReducer(Settings settings) {
        //todo add more
        return new LinearChainReducer(settings);
    }

    public static NetworkReducing getCompressor(Settings settings) {
        //todo add more
        return new IsoValueNetworkCompressor(settings);
    }

    static void supervisedNetPruning(DetailedNetwork<State.Neural.Structure> inet, BaseNeuron<Neuron, State.Neural> outputStart){
        Set<Neuron> visited = new HashSet<>();
        LinkedList<BaseNeuron<Neuron, State.Neural>> stack = new LinkedList<>();
        BaseNeuron<Neuron, State.Neural> outputStart1 = outputStart;
        inet.topoSortRecursive(outputStart1, visited, stack);

        List<BaseNeuron<Neuron, State.Neural>> reverse = new ArrayList<>(stack.size());
        Iterator<BaseNeuron<Neuron, State.Neural>> descendingIterator = stack.descendingIterator();
        descendingIterator.forEachRemaining(reverse::add);
        inet.allNeuronsTopologic = reverse;
    }
}
