package networks.structure.components.neurons.types;

import constructs.template.components.WeightedRule;
import networks.structure.metadata.states.State;
import networks.structure.components.neurons.BaseNeuron;

/**
 * Created by gusta on 8.3.17.
 */
public class RuleNeuron<S extends State.Neural> extends BaseNeuron<AtomFact, S> implements RuleNeurons<AtomFact, S> {

    public RuleNeuron(WeightedRule grounding, int index, S state) {
        super(index, grounding.toShortString(), state);
    }
}