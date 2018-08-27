package networks.structure.lrnnTypes;

import constructs.template.WeightedRule;
import networks.evaluation.values.Value;
import networks.structure.Neuron;

/**
 * Created by gusta on 8.3.17.
 */
public class AggregationNeuron extends Neuron<RuleNeuron> {

    public AggregationNeuron(WeightedRule groundRule) {
        super(groundRule.toString());
        this.activation = groundRule.aggregationFcn;
        this.offset = null;
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
