package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.Arrays;
import java.util.logging.Logger;

public class Reshape implements Transformation {
    private static final Logger LOG = Logger.getLogger(Reshape.class.getName());

    private final int[] shape;

    public Reshape(int[] shape) {
        if (shape == null) {
            shape = new int[] {0};
        }

        if (shape.length > 2) {
            String err = "Unsupported shape: " + Arrays.toString(shape) + ". Expected max two elements.";
            LOG.severe(err);
            throw new ArithmeticException(err);
        }

        this.shape = shape;
    }

    @Override
    public ActivationFcn replaceWithSingleton() {
        return null;
    }

    public Value evaluate(Value combinedInputs) {
        return combinedInputs.reshape(this.shape);
    }

    public Value differentiate(Value combinedInputs) {
        return null; // Shouldn't be called
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(this);
    }

    @Override
    public boolean changesShape() {
        return true;
    }

    public static class State extends Transformation.State {
        private final Reshape reshape;

        public State(Reshape transformation) {
            super(transformation);

            reshape = transformation;
        }

        @Override
        public void invalidate() {
            super.invalidate();
        }

        @Override
        public Value evaluate() {
            return reshape.evaluate(input);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            if (input instanceof ScalarValue) {
                processedGradient = topGradient.reshape(new int[] { 0 });
            } else if (input instanceof VectorValue) {
                VectorValue v = (VectorValue) input;
                final int len = v.values.length;

                processedGradient = topGradient.reshape(new int[] { v.rowOrientation ? len : 0, v.rowOrientation ? 0 : len});
            } else if (input instanceof MatrixValue) {
                MatrixValue m = (MatrixValue) input;

                processedGradient = topGradient.reshape(new int[] { m.rows, m.cols });
            }
        }
    }
}
