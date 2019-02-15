package networks.structure.transforming;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * Transfer parents count from neuron's computation state to neural net's storage
 */
public class ParentsExtractor {
    private static final Logger LOG = Logger.getLogger(ParentsExtractor.class.getName());


    //need to extract ParentCounting from neurons to networks in case of DFS and shared neurons - need to terminate the stream (since a neuron may become shared later on)
    public NeuralNetwork<State.Structure> transferSharedNeuronsParents(DetailedNetwork<State.Structure> neuralNetwork) {
        for (int i = 0; i < neuralNetwork.allNeuronsTopologic.size(); i++) {
            BaseNeuron<Neuron, State.Neural> n = neuralNetwork.allNeuronsTopologic.get(i);
            if (n.isShared && n.getRawState() instanceof State.Neural.Computation.HasParents) {
                State.Neural.Computation.HasParents computationParents = (State.Neural.Computation.HasParents) n.getRawState();
                setStructureParents(neuralNetwork.getState(i), computationParents);
            }
        }
        return neuralNetwork;
    }

    /**
     * Transfer parents count from neuron's structure to neural net's storage
     * @param structureState
     * @param computationParents
     */
    private void setStructureParents(State.Structure structureState, State.Neural.Computation.HasParents computationParents) {
        if (structureState instanceof State.Structure.Parents) {
            ((State.Structure.Parents) structureState).setParentCount(computationParents.getParents(null));
        } else {
            LOG.severe("Cannot transfer parentCount from neuron to network - invalid settings of StructureStates.");
        }
    }
}
