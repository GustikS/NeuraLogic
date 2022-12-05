package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.HashMap;
import java.util.Map;

public class SplittableAggregationNeuron<S extends State.Neural> extends AggregationNeuron<S> {

    public Map<String, AtomNeuron> inputOrder = new HashMap<>();

    public SplittableAggregationNeuron(String groundRule, int index, S state) {
        super(groundRule, index, state);
    }

    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit((AggregationNeuron<? extends State.Neural>) this);
    }
}
