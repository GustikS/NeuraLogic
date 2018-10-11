package networks.structure.neurons.creation;

import constructs.template.WeightedRule;
import networks.structure.networks.State;
import networks.structure.neurons.Neuron;

/**
 * Created by gusta on 8.3.17.
 */
public class AggregationNeuron<S extends State.Computation> extends Neuron<RuleNeurons, S> {

    public AggregationNeuron(WeightedRule groundRule, int index, S state) {
        super(index, groundRule.toString(), state, groundRule.aggregationFcn);
    }
}
