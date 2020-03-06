package networks.computation.iteration.visitors.states.networks;

import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.neurons.Neurons;
import networks.structure.metadata.inputMappings.NeuronMapping;
import networks.structure.components.neurons.states.State;

import java.util.logging.Logger;

public class OutputsGetter extends StateVisiting.Structure<NeuronMapping<Neurons>> {
    private static final Logger LOG = Logger.getLogger(OutputsGetter.class.getName());

    public NeuronMapping<Neurons> visit(State.Structure.OutputNeuronMap state) {
        return state.getOutputMapping();
    }
}
