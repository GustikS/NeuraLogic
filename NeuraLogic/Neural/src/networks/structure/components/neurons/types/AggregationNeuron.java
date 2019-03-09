package networks.structure.components.neurons.types;

import constructs.template.components.WeightedRule;
import networks.structure.metadata.states.State;
import networks.structure.components.neurons.BaseNeuron;

/**
 * Created by gusta on 8.3.17.
 */
public class AggregationNeuron<S extends State.Neural> extends BaseNeuron<RuleNeurons, S> {

    public AggregationNeuron(WeightedRule groundRule, int index, S state) {
        super(index, groundRule.originalString, state);
    }
}
