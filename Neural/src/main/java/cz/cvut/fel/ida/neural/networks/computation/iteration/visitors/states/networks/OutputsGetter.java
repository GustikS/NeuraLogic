package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.networks;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.NeuronMapping;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.logging.Logger;

public class OutputsGetter extends StateVisiting.Structure<NeuronMapping<Neurons>> {
    private static final Logger LOG = Logger.getLogger(OutputsGetter.class.getName());

    public NeuronMapping<Neurons> visit(State.Structure.OutputNeuronMap state) {
        return state.getOutputMapping();
    }
}
