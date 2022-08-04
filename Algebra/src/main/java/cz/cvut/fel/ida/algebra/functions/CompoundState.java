package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.Value;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Aggregation + Transformation on top
 */
public class CompoundState implements ActivationFcn.State {
    private static final Logger LOG = Logger.getLogger(CompoundState.class.getName());

    @NotNull
    Combination.State combinationState;
    @NotNull
    Transformation.State transformationState;

    public CompoundState(Combination.State combinationState, Transformation.State transformationState) {
        this.combinationState = combinationState;
        this.transformationState = transformationState;
    }

    @Override
    public void invalidate() {
        combinationState.invalidate();
        transformationState.invalidate();
    }

    @Override
    public void setupDimensions(Value value) {
        combinationState.setupDimensions(value);
        Value evaluate = combinationState.evaluate();
        transformationState.setupDimensions(evaluate);
    }

    @Override
    public Value evaluate() {
        Value evaluate = combinationState.evaluate();
        transformationState.cumulate(evaluate);
        return transformationState.evaluate();
    }

    @Override
    public void cumulate(Value value) {
        combinationState.cumulate(value);
    }

    @Override
    public void ingestTopGradient(Value topGradient) {
        transformationState.ingestTopGradient(topGradient);
        Value gradient = transformationState.gradient();
        combinationState.ingestTopGradient(gradient);
    }

    @Override
    public Value nextInputDerivative() {
        return combinationState.nextInputDerivative();
    }

    @Override
    public int[] getInputMask() {
        return combinationState.getInputMask();
    }

    @Override
    public Combination getCombination() {
        return combinationState.getCombination();
    }

    @Override
    public void setCombination(Combination combination) {
        combinationState.setCombination(combination);
    }

    @Override
    public Transformation getTransformation() {
        return transformationState.getTransformation();
    }

    @Override
    public void setTransformation(Transformation transformation) {
        transformationState.setTransformation(transformation);
    }


}
