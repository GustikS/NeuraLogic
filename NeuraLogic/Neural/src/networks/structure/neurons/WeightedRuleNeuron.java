package networks.structure.neurons;

import constructs.template.WeightedRule;
import networks.structure.WeightedNeuron;

import java.util.logging.Logger;

public class WeightedRuleNeuron extends WeightedNeuron<AtomFact> implements RuleNeurons {
    private static final Logger LOG = Logger.getLogger(WeightedRuleNeuron.class.getName());

    public WeightedRuleNeuron(WeightedRule grounding) {
        super(grounding.toString());
        this.activation = grounding.activationFcn;
    }
}
