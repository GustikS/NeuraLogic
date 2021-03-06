package cz.cvut.fel.ida.neural.networks.structure.transforming;

import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.setup.Settings;

import java.util.*;

/**
 * Created by gusta on 9.3.17.
 */
public interface NetworkReducing extends Exportable {

    NeuralNetwork reduce(DetailedNetwork<State.Neural.Structure> inet, List<QueryNeuron> outputStart);

    NeuralNetwork reduce(DetailedNetwork<State.Neural.Structure> inet, QueryNeuron outputStart);

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
    static void supervisedNetReconstruction(DetailedNetwork<State.Neural.Structure> inet, BaseNeuron<Neurons, State.Neural> outputStart){
        Set<Neurons> visited = new HashSet<>();
        LinkedList<BaseNeuron<Neurons, State.Neural>> stack = new LinkedList<>();
        BaseNeuron<Neurons, State.Neural> outputStart1 = outputStart;
        inet.topoSortRecursive(outputStart1, visited, stack);

        List<BaseNeuron<Neurons, State.Neural>> reverse = new ArrayList<>(stack.size());
        Iterator<BaseNeuron<Neurons, State.Neural>> descendingIterator = stack.descendingIterator();
        descendingIterator.forEachRemaining(reverse::add);
        inet.allNeuronsTopologic = reverse;
    }

    static void supervisedNetReconstruction(DetailedNetwork<State.Neural.Structure> inet, List<BaseNeuron<Neurons, State.Neural>> allQueryNeurons){
        inet.allNeuronsTopologic = inet.topologicSort(allQueryNeurons);
    }

    void finish();
}
