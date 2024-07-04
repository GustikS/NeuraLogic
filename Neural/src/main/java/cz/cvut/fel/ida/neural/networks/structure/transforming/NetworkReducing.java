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
     * @param allQueryNeurons
     */
    static void supervisedNetReconstruction(DetailedNetwork<State.Neural.Structure> inet, List<Neurons> allQueryNeurons){
        if (allQueryNeurons == null || allQueryNeurons.isEmpty() || allQueryNeurons.get(0) == null){
            allQueryNeurons = new ArrayList<>(inet.allNeuronsTopologic);
        }
        inet.allNeuronsTopologic = inet.new TopoSorting().topologicSort(allQueryNeurons);
    }

    void finish();
}
