package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.XMax;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.math.VectorUtils;

import java.util.List;
import java.util.logging.Logger;

/**
 * This is both Transformation (joint activation) and Combination...
 * todo test softmax as an combination fcn
 */
public class Softmax implements Transformation, Combination, XMax, Aggregation {
    private static final Logger LOG = Logger.getLogger(Softmax.class.getName());

    private final int[] aggregableTerms;

    public Softmax() {
        this.aggregableTerms = null;
    }

    public Softmax(int[] aggregableTerms) {
        this.aggregableTerms = aggregableTerms;
    }

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

    /**
     * Inefficient, we should remember the probabilities
     *
     * @param summedInputs
     * @return
     */
    public Value differentiate(Value summedInputs) {
        if (summedInputs instanceof VectorValue) {
            VectorValue inputVector = (VectorValue) summedInputs;
            double[] exps = getProbabilities(inputVector.values);
            double[] diffs = getGradient(exps);
            return new MatrixValue(diffs, exps.length, exps.length);
        } else {
            throw new ClassCastException("Trying to differentiate softmax on something else than a Vector...");
        }
    }

    /**
     * We assume a list of ScalarValues here!
     *
     * @param inputs
     * @return
     */
    @Override
    public Value evaluate(List<Value> inputs) {
        double[] exps = getProbabilities(inputs);
        VectorValue output = new VectorValue(exps);
        return output;
    }


    public double[] getGradient(double[] exps) {
        final double[] diffs = new double[exps.length * exps.length];
        for (int i = 0; i < exps.length; i++) {
            final int tmpIndex = i * exps.length;

            for (int j = 0; j < exps.length; j++) {
                if (i == j) {
                    diffs[tmpIndex + j] = exps[i] * (1 - exps[j]);
                } else {
                    diffs[tmpIndex + j] = -exps[i] * exps[j];
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
    public boolean isPermutationInvariant() {
        return false;
    }

    @Override
    public boolean isSplittable() {
        return aggregableTerms != null && aggregableTerms.length != 0;
    }

    @Override
    public int[] aggregableTerms() {
        return aggregableTerms;
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        LOG.warning("Directly calculating derivative of SOFTMAX fcn");
        return Value.ONE;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    /**
     * Softmax for a single scalar input value = constant 1
     * @return
     */
    @Override
    public Transformation singleInputVersion() {
        return Transformation.Singletons.constantOne;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        if (singleInput)
            return new TransformationState(Transformation.Singletons.softmax);
        else
            return new CombinationState(Transformation.Singletons.softmax); //let's reuse the same Transformation singleton in both cases
    }

    public static class TransformationState extends Transformation.State {

        double[] probabilities;

        public TransformationState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            probabilities = null;
        }

        @Override
        public Value evaluate() {
            XMax xMax = (XMax) transformation;
            probabilities = xMax.getProbabilities(((VectorValue) input).values);
            return new VectorValue(probabilities);
        }

        public Value gradient() {
            XMax xMax = (XMax) transformation;
            double[] gradient = xMax.getGradient(probabilities);
            return new MatrixValue(gradient, probabilities.length, probabilities.length);
        }
    }

    public static class CombinationState extends Combination.InputArrayState {

        double[] probabilities;

        public CombinationState(Combination combination) {
            super(combination);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            probabilities = null;
        }

        @Override
        public Value evaluate() {
            XMax xMax = (XMax) combination;
            probabilities = xMax.getProbabilities(accumulatedInputs);
            return new VectorValue(probabilities);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            Value inputFcnDerivative = gradient();
            processedGradient = inputFcnDerivative.times(topGradient);  //times here - since the fcn was a complex vector function (e.g. softmax) and has a matrix derivative (Jacobian)
        }

        @Override
        public Value nextInputGradient() {
            return new ScalarValue(processedGradient.get(i++));
        }

        public Value gradient() {
            XMax xMax = (XMax) combination;
            double[] gradient = xMax.getGradient(probabilities);
            return new MatrixValue(gradient, probabilities.length, probabilities.length);
        }
    }
}
