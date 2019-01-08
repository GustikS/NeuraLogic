package networks.computation.iteration.actions;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.WeightVisitor;
import networks.structure.components.weights.StatefulWeight;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

public class WeightUpdater extends WeightVisitor {
    private static final Logger LOG = Logger.getLogger(WeightUpdater.class.getName());

    /**
     *  To be used instead of storing the gradient update in the weight object (StetefulWeight) for PARALLEL backproping.
     *  Since the number of all unique weights is typically low, each thread has its own full index of weightUpdates.
     *
     * UNSYCHRONIZED storage of weight updates.
     */
    Value[] weightUpdates;

    public WeightUpdater(Evaluator evaluator) {
        super(evaluator);
    }

    @Override
    public void visit(Weight weight, Value value) {
        weightUpdates[weight.index].increment(value);
    }

    @Override
    public void visit(Weight weight, Value gradient, State.Neural.Computation input) {
        weightUpdates[weight.index].increment(evaluator.visit(input));
    }

    public void visit(StatefulWeight weight, Value value) {
        weight.getAccumulatedUpdate().increment(value); //todo this will probably never get called, is StatefulWeight necessary?
    }
}
