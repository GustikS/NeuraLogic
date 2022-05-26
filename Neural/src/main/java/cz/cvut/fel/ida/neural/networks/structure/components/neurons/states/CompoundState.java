package cz.cvut.fel.ida.neural.networks.structure.components.neurons.states;

import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

/**
 * Aggregation + Transformation on top
 */
public class CompoundState {
    private static final Logger LOG = Logger.getLogger(CompoundState.class.getName());

    @Override
    public Value evaluate() {
        if (transformation != null)
            return transformation.evaluate(combinedInputs);
        else
            return combinedInputs;
    }

    @Override
    public Value gradient() {
        if (transformation != null) {
            transformedGradient = transformation.differentiate(combinedInputs);
        } else {
            transformedGradient = Value.ONE;
        }
        return transformedGradient;
    }
}
