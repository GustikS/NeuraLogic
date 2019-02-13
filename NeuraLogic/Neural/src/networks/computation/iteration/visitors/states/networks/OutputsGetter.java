package networks.computation.iteration.visitors.states.networks;

import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.inputMappings.NeuronMapping;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

public class OutputsGetter extends StateVisiting.Structure<NeuronMapping<Neuron>> {
    private static final Logger LOG = Logger.getLogger(OutputsGetter.class.getName());

    public NeuronMapping<Neuron> visit(State.Structure.OutputNeuronMap state) {
        return state.getOutputMapping();
    }
}
