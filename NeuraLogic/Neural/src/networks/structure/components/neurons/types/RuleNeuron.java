package networks.structure.components.neurons.types;

import constructs.template.components.WeightedRule;
import networks.structure.metadata.states.State;
import networks.structure.components.neurons.Neuron;

/**
 * Created by gusta on 8.3.17.
 */
public class RuleNeuron<S extends State.Computation> extends Neuron<AtomFact, S> implements RuleNeurons {

    public RuleNeuron(WeightedRule grounding, int index, S state) {
        super(index, grounding.toString(), state, grounding.activationFcn);
    }
}