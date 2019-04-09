package networks.structure.components.neurons.types;

import constructs.template.components.WeightedRule;
import networks.structure.metadata.states.State;
import networks.structure.components.neurons.WeightedNeuron;

import java.util.logging.Logger;

public class WeightedRuleNeuron<S extends State.Neural> extends WeightedNeuron<AtomFact, S> implements RuleNeurons<AtomFact, S> {
    private static final Logger LOG = Logger.getLogger(WeightedRuleNeuron.class.getName());

    public WeightedRuleNeuron(WeightedRule grounding, int index, S state) {
        super(grounding.toString(), index, state, grounding.getOffset());
    }
}
