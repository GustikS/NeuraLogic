package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.transformation.joint.Identity;
import cz.cvut.fel.ida.algebra.values.Value;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Aggregation + Transformation on top
 */
public class CompoundState implements ActivationFcn.State {
    private static final Logger LOG = Logger.getLogger(CompoundState.class.getName());

    @NotNull
    private final Combination.State combinationState;
    @NotNull
    private Transformation.State transformationState;

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
    public Value initEval(List<Value> values) {
        Value value = combinationState.initEval(values);
        List<Value> vals = new ArrayList<>(1);
        vals.add(value);
        return transformationState.initEval(vals);
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
        Value transformationGradient = transformationState.nextInputGradient();
        combinationState.ingestTopGradient(transformationGradient);
    }

    @Override
    public Value nextInputGradient() {
        return combinationState.nextInputGradient();
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
    public Transformation getTransformation() {
        return transformationState.getTransformation();
    }

    @Override
    public ActivationFcn.State changeTransformationState(Transformation transformation) {
        if (transformation.getClass().equals(this.transformationState.transformation.getClass())) {
            return this;    // no change
        } else if (transformation instanceof Identity) {
            return this.combinationState;   // no transformation anymore
        } else {
            this.transformationState = (Transformation.State) transformation.getState(true);    // switch to the new transformation (no stateful values there)
            return this;
        }
    }
}