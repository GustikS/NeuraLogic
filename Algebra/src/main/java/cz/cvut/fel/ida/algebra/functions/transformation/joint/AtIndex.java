package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.logging.Logger;

public class AtIndex implements Transformation {
    private static final Logger LOG = Logger.getLogger(AtIndex.class.getName());

    private int index;

    public AtIndex() {}

    public AtIndex(int index) {
        if (index < 0) {
            String err = "Invalid AtIndex index: " + index + ". Must be >= 0.";
            LOG.severe(err);
            throw new ArithmeticException(err);
        }

        this.index = index;
    }

    public void setIndex(int index) { // Maybe add validation
        this.index = index;
    }

    @Override
    public ActivationFcn replaceWithSingleton() {
        return null;
    }

    public Value evaluate(Value combinedInputs) {
        return new ScalarValue(combinedInputs.get(this.index));
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
        private final AtIndex atIndex;

        public State(AtIndex transformation) {
            super(transformation);

            atIndex = transformation;
        }

        @Override
        public void invalidate() {
            super.invalidate();
        }

        @Override
        public Value evaluate() {
            return atIndex.evaluate(input);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            if (input instanceof ScalarValue) {
                processedGradient = topGradient;

                return;
            }

            final double[] grad = new double[input.getAsArray().length];
            grad[atIndex.index] = topGradient.get(0);

            if (input instanceof VectorValue) {
                processedGradient = new VectorValue(grad, ((VectorValue) input).rowOrientation);

                return;
            }

            MatrixValue m = (MatrixValue) input;
            processedGradient = new MatrixValue(grad, m.rows, m.cols);
        }
    }
}
