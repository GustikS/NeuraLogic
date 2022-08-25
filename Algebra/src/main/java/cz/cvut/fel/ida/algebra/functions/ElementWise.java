package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.*;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Class representing simple activation functions, i.e. those that element-wise apply some non-linearity on top of some Value.
 * The non-linearity is provided via FunctionalInterface Function separately for evaluation and derivative/gradient.
 */
public abstract class ElementWise implements Transformation {
    private static final Logger LOG = Logger.getLogger(ElementWise.class.getName());

    /**
     * Forward-pass function
     */
    transient DoubleUnaryOperator evaluation;
    /**
     * Backward-pass / derivative of the evaluation function
     */
    transient DoubleUnaryOperator gradient;


    protected ElementWise(DoubleUnaryOperator evaluation, DoubleUnaryOperator gradient) {
        this.evaluation = evaluation;
        this.gradient = gradient;
    }

    /**
     * We apply element-wise here
     * @param combinedInputs
     * @return
     */
    public Value evaluate(Value combinedInputs) {
        return combinedInputs.apply(evaluation);
    }

    /**
     * We apply element-wise here
     * @param combinedInputs
     * @return
     */
    public Value differentiate(Value combinedInputs) {
        return combinedInputs.apply(gradient);
    }

    public static ElementWise getFunction(Settings.TransformationFcn activationFcn) {
        switch (activationFcn) {
            case SIGMOID:
                return Singletons.sigmoid;
            case TANH:
                return Singletons.tanh;
            case SIGNUM:
                return Singletons.signum;
            case RELU:
                return Singletons.relu;
            case LEAKYRELU:
                return Singletons.leakyRelu;
            case LUKASIEWICZ:
                return Singletons.lukasiewiczSigmoid;
            case EXP:
                return Singletons.exponentiation;
            case SQRT:
                return Singletons.sqrt;
            case INVERSE:
                return Singletons.inverse;
            case REVERSE:
                return Singletons.reverse;
            case LOGARITHM:
                return Singletons.logarithm;
            default:
//                LOG.severe("Unimplemented activation function");
                return null;
        }
    }


    public static class Singletons {
        public static LukasiewiczSigmoid lukasiewiczSigmoid = new LukasiewiczSigmoid();
        public static Sigmoid sigmoid = new Sigmoid();
        public static Signum signum = new Signum();
        public static ReLu relu = new ReLu();
        public static LeakyReLu leakyRelu = new LeakyReLu();
        public static Tanh tanh = new Tanh();
        public static Exponentiation exponentiation = new Exponentiation();
        public static SquareRoot sqrt = new SquareRoot();
        public static Inverse inverse = new Inverse();
        public static Reverse reverse = new Reverse();
        public static Logarithm logarithm = new Logarithm();
    }

    @Override
    public Transformation.State getState(boolean singleInput) {
        return new State(this);
    }

    public static class State extends Transformation.State {

        public State(ElementWise elementWise) {
            super(elementWise);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            Value inputFcnDerivative = gradient();
            inputFcnDerivative.elementMultiplyBy(topGradient); //elementMultiplyBy here - since the fcn to be differentiated was applied element-wise on a vector

            processedGradient = inputFcnDerivative;
        }
    }
}