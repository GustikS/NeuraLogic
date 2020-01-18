package networks.structure.building.factories;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.NeuronSets;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.types.AtomNeurons;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.metadata.inputMappings.LinkedMapping;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.*;
import java.util.logging.Logger;

public class NeuralNetFactory {
    private static final Logger LOG = Logger.getLogger(NeuralNetFactory.class.getName());
    Settings settings;

    public NeuralNetFactory(Settings settings) {
        this.settings = settings;
    }

    /**
     * @return
     */
    public <S extends State.Structure> NeuralNetwork<S> extractOptimizedNetwork(DetailedNetwork<S> network) {
        if (!settings.parentCounting) {
            return extractTopologicNetwork(network);
        } else {
            return new NeuralNetwork<>(network.getId(), network.getNeuronCount());
        } //todo maybe add an even more optimized version? (everything based on int, precompute layers - probably not as that should probably go for export to tensorflow or dynet)
    }

    /**
     * Strips all the unnecessary info from the network to optimize for memory.
     *
     * @param network
     * @return
     */
    public <S extends State.Structure> TopologicNetwork<S> extractTopologicNetwork(DetailedNetwork<S> network) {
        TopologicNetwork<S> topologicNetwork = new TopologicNetwork<>(network.getId(), network.allNeuronsTopologic, true);
        topologicNetwork.hasSharedNeurons = network.hasSharedNeurons;
        topologicNetwork.containsInputMasking = network.containsInputMasking;
        topologicNetwork.containsCrossProducts = network.containsCrossProducts;
        topologicNetwork.neuronStates = network.neuronStates;
        return topologicNetwork;
    }

    public DetailedNetwork createDetailedNetwork(List<AtomNeurons> queryNeurons, NeuronSets createdNeurons, String id, Map<Neurons, LinkedMapping> extraInputMapping) {
        DetailedNetwork detailedNetwork;
        if (queryNeurons != null) {
            detailedNetwork = new DetailedNetwork(id, createdNeurons, queryNeurons);
        } else {
            detailedNetwork = new DetailedNetwork(id, createdNeurons.getAllNeurons(), createdNeurons);
        }

        if (!settings.possibleNeuronSharing) {
            detailedNetwork.sortIndices();  // only for independent networks
        }

        //we take input overmaps from the neuronMaps, otherwise the inputs of the previously created neurons would be wrong
        if (extraInputMapping != null && !extraInputMapping.isEmpty()) {
            detailedNetwork.extraInputMapping = new HashMap<>();
            Set<Neurons> currentNeurons = new HashSet<>(detailedNetwork.allNeuronsTopologic);
            for (Map.Entry<Neurons, LinkedMapping> entry : extraInputMapping.entrySet()) {
                if (currentNeurons.contains(entry.getKey()))
                    detailedNetwork.extraInputMapping.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        return detailedNetwork;
    }
}