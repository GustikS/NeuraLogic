package networks.structure.components.neurons.types;

import constructs.template.components.WeightedRule;
import networks.structure.metadata.states.State;
import networks.structure.components.neurons.Neuron;

/**
 * Created by gusta on 8.3.17.
 */
public class AggregationNeuron<S extends State.Computation> extends Neuron<RuleNeurons, S> {

    public AggregationNeuron(WeightedRule groundRule, int index, S state) {
        super(index, groundRule.toString(), state, groundRule.aggregationFcn);
    }
}
