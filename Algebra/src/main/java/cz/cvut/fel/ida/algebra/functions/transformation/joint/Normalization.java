package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.logging.Logger;

/**
 * Pure Layer normalization (standardization) without scaling/shifting
 */
public class Normalization implements Transformation {
    private static final Logger LOG = Logger.getLogger(Normalization.class.getName());

    /**
     * for division by zero in standardization for constant tensor
     */
    static double eps = 1e-10;

    @Override
    public ActivationFcn replaceWithSingleton() {
        return Singletons.normalization;
    }

    public Value evaluate(Value combinedInputs) {
        double sum = 0;
        int count = 0;
        for (Double summedInput : combinedInputs) {
            sum += summedInput;
            count++;
        }
        double mean = sum / count;

        double var = 0; // avoid div by zero for constant tensor
        for (Double input : combinedInputs) {
            double diff = (input - mean);
            var += diff * diff;
        }
        var = var / count;
        double stdEps = Math.sqrt(var + eps);
        return combinedInputs.apply(x -> (x - mean) / stdEps);
    }

    /**
     * Inefficient as needs to evaluate again from scratch.
     * But the @State will be used during computation instead anyway.
     * @param combinedInputs
     * @return
     */
    public Value differentiate(Value combinedInputs) {
        // first eval to create the necessary values (mean,var,...)
        double sum = 0;
        int count = 0;
        for (Double summedInput : combinedInputs) {
            sum += summedInput;
            count++;
        }
        double mean = sum / count;

        double var = 0;
        for (Double input : combinedInputs) {
            double diff = (input - mean);
            var += diff * diff;
        }
        var = var / count;
        double stdEps = Math.sqrt(var + eps);

        // gradient from here
        double gradSigma = 0;
        for (Double x : combinedInputs) {
            gradSigma += (x - mean);
        }
        gradSigma *= -0.5 * Math.pow(var + eps, -1.5);

        double invCount = 1.0 / count;
        double gradMean = 0;
        for (Double x : combinedInputs) {
            gradMean += -1 / stdEps + gradSigma * invCount * 2 * (x - mean) * -1;
        }

        int i = 0;
        double[] grad = new double[count];
        for (Double x : combinedInputs) {
            grad[i] = 1 / stdEps + gradSigma * invCount * 2 * (x - mean) + gradMean * invCount;
        }
        return new VectorValue(grad);
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(Singletons.normalization);
    }

    public static class State extends Transformation.State {

        double mean;
        double var;
        int count;
        double stdEps;

        public State(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mean = 0;
            var = 0;
            count = 0;
            stdEps = 0;
        }

        @Override
        public Value evaluate() {
            double sum = 0;
            count = 0;
            for (Double summedInput : input) {
                sum += summedInput;
                count++;
            }
            mean = sum / count;

            var = 0;
            for (Double input : input) {
                double diff = (input - mean);
                var += diff * diff;
            }
            var = var / count;
            stdEps = Math.sqrt(var + eps);
            return input.apply(x -> (x - mean) / stdEps);
        }

        /**
         * Checked against https://towardsdatascience.com/implementing-spatial-batch-instance-layer-normalization-in-tensorflow-manual-back-prop-in-tf-77faa8d2c362
         * @param topGradient
         */
        @Override
        public void ingestTopGradient(Value topGradient) {
            double gradSigma = 0;
            for (int j = 0; j < count; j++) {
                gradSigma += topGradient.get(j) * (input.get(j) - mean);
            }
            gradSigma *= -0.5 * Math.pow(var + eps, -1.5);

            double invCount = 1.0 / count;
            double gradMean = 0;
            for (int j = 0; j < count; j++) {
                gradMean += (topGradient.get(j) * (-1 / stdEps)) + gradSigma * invCount * -2 * (input.get(j) - mean);
            }

            double[] grad = new double[count];
            for (int j = 0; j < count; j++) {
                grad[j] = topGradient.get(j) * (1 / stdEps) + gradSigma * invCount * 2.0 * (input.get(j) - mean) + gradMean * invCount;
            }
            processedGradient = new VectorValue(grad);
        }
    }
}
