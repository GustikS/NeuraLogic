package cz.cvut.fel.ida.neural.networks.structure.components.neurons.states;

import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.SharpMax;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.SharpMin;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.Softmax;

import java.util.logging.Logger;

/**
 * A state corresponding to complex Transformation functions    todo now now and skip whenever possible
 */
public class TransformationState {
    private static final Logger LOG = Logger.getLogger(TransformationState.class.getName());

    public static TransformationState get(Transformation transformation) {
        if (transformation instanceof Softmax) {
            return new SoftmaxState();
        } else if (transformation instanceof SharpMax) {
            return new SharpMaxState();
        } else if (transformation instanceof SharpMin) {
            return new SharpMinState();
        } else return new TransformationState();
    }

    public static class SharpMaxState extends TransformationState {
        int maxIndex = -1;
        int currentIndex = 0;

    }

    public static class SharpMinState extends TransformationState {
        int minIndex = -1;
        int currentIndex = 0;

    }

    public static class SoftmaxState extends TransformationState {
    }
}
