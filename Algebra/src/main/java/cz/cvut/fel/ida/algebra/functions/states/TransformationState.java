package cz.cvut.fel.ida.algebra.functions.states;

import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

/**
 * A state corresponding to complex Transformation functions    todo now now and skip whenever possible
 */
public class TransformationState implements Transformation.State {
    private static final Logger LOG = Logger.getLogger(TransformationState.class.getName());

    Value gradient;

    @Override
    public void setupDimensions(Value value) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public Value evaluate() {
        return null;
    }

    @Override
    public void cumulate(Value value) {

    }

    @Override
    public void ingestTopGradient(Value topGradient) {

    }

    @Override
    public Value nextInputDerivative() {
        return null;
    }

    @Override
    public int[] getInputMask() {
        return new int[0];
    }

    @Override
    public Combination getCombination() {
        return null;
    }

    @Override
    public void setCombination(Combination combination) {

    }

    @Override
    public Transformation getTransformation() {
        return null;
    }

    @Override
    public void setTransformation(Transformation transformation) {

    }

//    public static TransformationState get(Transformation transformation) {
//        if (transformation instanceof Softmax) {
//            return new SoftmaxState();
//        } else if (transformation instanceof SharpMax) {
//            return new SharpMaxState();
//        } else if (transformation instanceof SharpMin) {
//            return new SharpMinState();
//        } else return new TransformationState();
//    }

//    public static class SharpMaxState extends TransformationState {
//        int maxIndex = -1;
//        int currentIndex = 0;
//
//    }
//
//    public static class SharpMinState extends TransformationState {
//        int minIndex = -1;
//        int currentIndex = 0;
//
//    }
//
//    public static class SoftmaxState extends TransformationState {
//    }
}
