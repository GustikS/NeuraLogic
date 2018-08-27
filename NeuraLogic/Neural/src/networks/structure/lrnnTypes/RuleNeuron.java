package networks.structure.lrnnTypes;

import constructs.template.WeightedRule;
import networks.evaluation.values.Value;
import networks.structure.Neuron;

/**
 * Created by gusta on 8.3.17.
 */
public class RuleNeuron extends Neuron<AtomFact> {

    public RuleNeuron(WeightedRule grounding) {
        super(grounding.toString());
        this.activation = grounding.activationFcn;
        this.offset = grounding.offset;
    }

    @Override
    public Value evaluate() {
        return null;
    }

    @Override
    public Value gradient() {
        return null;
    }
}
