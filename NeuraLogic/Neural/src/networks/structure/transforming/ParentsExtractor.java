package networks.structure.transforming;

import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.metadata.states.State;
import networks.structure.metadata.states.States;

import java.util.logging.Logger;

/**
 * Transfer parents count from neuron's computation state to neural net's storage
 */
public class ParentsExtractor {
    private static final Logger LOG = Logger.getLogger(ParentsExtractor.class.getName());

    //need to extract ParentCounting from neurons to networks in case of DFS and shared neurons - need to terminate the stream (since a neuron may become shared later on)
    public void extractSharedNeuronsParents(DetailedNetwork<State.Structure> neuralNetwork) {
        for (int i = 0; i < neuralNetwork.allNeuronsTopologic.size(); i++) {
            BaseNeuron<Neuron, State.Neural> neuron = neuralNetwork.allNeuronsTopologic.get(i);
            if (neuron.sharedAfterCreation) {
                State.Neural.Computation state = neuron.getComputationView(0); //all computation views should be exactly the same at this stage
                if (state instanceof State.Neural.Computation.HasParents) {
                    State.Neural.Computation.HasParents computationParents = (State.Neural.Computation.HasParents) state;
                    //Transfer parents count from neuron's structure to neural net's storage
                    //todo - it might be already in there, how to skip these?
                    neuralNetwork.addState(neuron, new States.NetworkParents(neuron.getRawState(), computationParents.getParents(null)));
                }
            }
        }
    }
}
