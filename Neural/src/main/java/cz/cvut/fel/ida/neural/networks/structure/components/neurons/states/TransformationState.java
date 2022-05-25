package cz.cvut.fel.ida.neural.networks.structure.components.neurons.states;

import java.util.logging.Logger;

/**
 * A state corresponding to complex Transformation functions    todo now now and skip whenever possible
 */
public class TransformationState {
    private static final Logger LOG = Logger.getLogger(TransformationState.class.getName());

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
