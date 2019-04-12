package networks.structure.components.neurons.types;

import networks.structure.components.neurons.BaseNeuron;
import networks.structure.metadata.states.State;

/**
 * Created by gusta on 8.3.17.
 */
public class AggregationNeuron<S extends State.Neural> extends BaseNeuron<RuleNeurons, S> {

    public AggregationNeuron(String groundRule, int index, S state) {
        super(index, groundRule, state);
    }
}
