package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.networks;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.NeuronMapping;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.logging.Logger;

public class InputsGetter extends StateVisiting.Structure<NeuronMapping<Neurons>> {
    private static final Logger LOG = Logger.getLogger(InputsGetter.class.getName());

    public NeuronMapping<Neurons> visit(State.Structure.InputNeuronMap state) {
        return state.getInputMapping();
    }

    public class Weighted extends StateVisiting.Structure<WeightedNeuronMapping<Neurons>> {

        public WeightedNeuronMapping<Neurons> visit(State.Structure.WeightedInputsMap state) {
            return state.getWeightedMapping();
        }
    }
}
