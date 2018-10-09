package networks.structure.neurons.creation;

import constructs.template.WeightedRule;
import networks.evaluation.iteration.State;
import networks.structure.neurons.WeightedNeuron;

import java.util.logging.Logger;

public class WeightedRuleNeuron<S extends State.Computation> extends WeightedNeuron<AtomFact, S> implements RuleNeurons {
    private static final Logger LOG = Logger.getLogger(WeightedRuleNeuron.class.getName());

    public WeightedRuleNeuron(WeightedRule grounding, int index, S state) {
        super(grounding.toString(), index, state, grounding.offset, grounding.activationFcn);
    }
}
