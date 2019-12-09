package networks.structure.transforming;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.types.AtomNeurons;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.*;

/**
 * Created by gusta on 9.3.17.
 */
public interface NetworkReducing {

    public abstract NeuralNetwork reduce(DetailedNetwork<State.Neural.Structure> inet, AtomNeurons<State.Neural> outputStart);

    public static NetworkReducing getReducer(Settings settings) {
        //todo add more
        return new LinearChainReducer(settings);
    }

    public static NetworkReducing getCompressor(Settings settings) {
        //todo add more
        return new IsoValueNetworkCompressor(settings);
    }

    public static NetworkReducing getEdgeMerger(Settings settings){
        return new ParallelEdgeMerger(settings);
    }

    /**
     * Rebuild the allNeuronsTopologic array
     * @param inet
     * @param outputStart
     */
    static void supervisedNetPruning(DetailedNetwork<State.Neural.Structure> inet, BaseNeuron<Neurons, State.Neural> outputStart){
        Set<Neurons> visited = new HashSet<>();
        LinkedList<BaseNeuron<Neurons, State.Neural>> stack = new LinkedList<>();
        BaseNeuron<Neurons, State.Neural> outputStart1 = outputStart;
        inet.topoSortRecursive(outputStart1, visited, stack);

        List<BaseNeuron<Neurons, State.Neural>> reverse = new ArrayList<>(stack.size());
        Iterator<BaseNeuron<Neurons, State.Neural>> descendingIterator = stack.descendingIterator();
        descendingIterator.forEachRemaining(reverse::add);
        inet.allNeuronsTopologic = reverse;
    }
}
