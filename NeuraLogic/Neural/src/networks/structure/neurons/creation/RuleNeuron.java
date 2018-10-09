package networks.structure.neurons.creation;

import constructs.template.WeightedRule;
import networks.evaluation.iteration.State;
import networks.structure.neurons.Neuron;

/**
 * Created by gusta on 8.3.17.
 */
public class RuleNeuron<S extends State.Computation> extends Neuron<AtomFact, S> implements RuleNeurons {

    public RuleNeuron(WeightedRule grounding, int index, S state) {
        super(index, grounding.toString(), state, grounding.activationFcn);
    }
}