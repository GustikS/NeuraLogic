package networks.structure.neurons;

import constructs.template.WeightedRule;
import networks.structure.Neuron;

/**
 * Created by gusta on 8.3.17.
 */
public class AggregationNeuron extends Neuron<RuleNeurons> {

    public AggregationNeuron(WeightedRule groundRule) {
        super(groundRule.toString());
        this.activation = groundRule.aggregationFcn;
    }
}
