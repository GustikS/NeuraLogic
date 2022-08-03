package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.math.VectorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is both Transformation (joint activation) and Aggregation...
 * todo test softmax as an aggregation fcn
 */
public class Softmax implements Transformation, Combination, XMax {
    private static final Logger LOG = Logger.getLogger(Softmax.class.getName());

    @Override
    public Transformation replaceWithSingleton() {
        return Transformation.Singletons.softmax;
    }

    public Value evaluate(Value combinedInputs) {
        if (combinedInputs instanceof VectorValue) {
            VectorValue inputVector = (VectorValue) combinedInputs;
            double[] probabilities = getProbabilities(inputVector.values);
            return new VectorValue(probabilities);
        } else {
            throw new ClassCastException("Trying to apply softmax on something else than a Vector...");
        }
    }

    public Value differentiate(Value summedInputs) {
        if (summedInputs instanceof VectorValue) {
            VectorValue inputVector = (VectorValue) summedInputs;
            double[] exps = getProbabilities(inputVector.values);
            double[][] diffs = getGradient(exps);
            return new MatrixValue(diffs);
        } else {
            throw new ClassCastException("Trying to differentiate softmax on something else than a Vector...");
        }
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        double[] exps = getProbabilities(inputs);
        VectorValue output = new VectorValue(exps);
        return output;
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        double[] exps = getProbabilities(inputs);
        double[][] diffs = getGradient(exps);
        MatrixValue output = new MatrixValue(diffs);
        return output;
    }


    public double[][] getGradient(double[] exps) {
        double[][] diffs = new double[exps.length][exps.length];
        for (int i = 0; i < exps.length; i++) {
            for (int j = 0; j < exps.length; j++) {
                if (i == j) {
                    diffs[i][j] = exps[i] * (1 - exps[j]);
                } else {
                    diffs[i][j] = -exps[i] * exps[j];
                }
            }
        }

        return diffs;
    }

    @Override
    public double[] getProbabilities(double[] input) {
        double max = VectorUtils.max(input);    //for numeric stability

        double expsum = 0;
        double[] exps = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            double exp = Math.exp(input[i] - max);
            exps[i] = exp;
            expsum += exp;
        }
        for (int i = 0; i < exps.length; i++) {
            exps[i] /= expsum;
        }
        return exps;
    }

    public double[] getProbabilities(List<Value> inputs) {
        if (inputs.size() == 1 && inputs.get(0) instanceof VectorValue) {
            return getProbabilities(((VectorValue) inputs.get(0)).values);
        }

        double max = getMax(inputs);    //for numeric stability

        double expsum = 0;
        double[] exps = new double[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            double exp = Math.exp(((ScalarValue) inputs.get(i)).value - max); //assuming softmax over scalars!
            exps[i] = exp;
            expsum += exp;
        }
        for (int i = 0; i < exps.length; i++) {
            exps[i] /= expsum;
        }
        return exps;
    }

    private double getMax(List<Value> inputs) {
        double max = Double.NEGATIVE_INFINITY;
        for (Value value : inputs) {
            if (((ScalarValue) value).value > max)
                max = ((ScalarValue) value).value;
        }
        return max;
    }

    @Override
    public boolean isInputSymmetric() {
        return false;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInputValue) {
        if (singleInputValue)
            return new TransformationState();
        else
            return new CombinationState();
    }

    public static class TransformationState extends Transformation.State {

        double[] probabilities;

        public TransformationState() {
            super(Transformation.Singletons.softmax);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            probabilities = null;
        }

        @Override
        public Value evaluate() {
            probabilities = Transformation.Singletons.softmax.getProbabilities(((VectorValue) input).values);
            return new VectorValue(probabilities);
        }

        @Override
        public Value gradient() {
            double[][] gradient = Transformation.Singletons.softmax.getGradient(probabilities);
            return new MatrixValue(gradient);
        }

    }

    public static class CombinationState extends Combination.State {

        ArrayList<Value> accumulatedInputs;
        double[] probabilities;
        int i = 0;

        public CombinationState() {
            super(Transformation.Singletons.softmax);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            accumulatedInputs.clear();
            probabilities = null;
            i = 0;
        }

        @Override
        public Value evaluate() {
            probabilities = Transformation.Singletons.softmax.getProbabilities(accumulatedInputs);
            return new VectorValue(probabilities);
        }

        @Override
        public void cumulate(Value value) {
            accumulatedInputs.add(value);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            Value inputFcnDerivative = gradient();
            processedGradient = inputFcnDerivative.times(topGradient);  //times here - since the fcn was a complex vector function (e.g. softmax) and has a matrix derivative (Jacobian)
        }

        @Override
        public Value nextInputDerivative() {
            return new ScalarValue(processedGradient.get(i++));
        }

        public Value gradient() {
            double[][] gradient = Transformation.Singletons.softmax.getGradient(probabilities);
            return new MatrixValue(gradient);
        }
    }
}
