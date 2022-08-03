package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.*;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Class representing simple activation functions, i.e. those that element-wise apply some non-linearity on top of some Value.
 * The non-linearity is provided via FunctionalInterface Function separately for evaluation and derivative/gradient.
 * <p>
 * Steps for adding new activation/aggregation:
 * 1) add definition of the function (evaluation+differentiation) by overriding {@link ElementWise} or {@link Aggregation} class
 * 2) create a static singleton in {@link ElementWise} or {@link Aggregation} for reuse, if possible
 * 3) update {@link ElementWise#parseFrom(String)} and {@link ElementWise#getFunction(Settings.ActivationFcn)} with the new option
 * 4) if beneficial/required, create new computation state in {cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.AggregationState} for the function
 * 5) if the function requires special backprop treatment, create new Visitors.Down propagator and update ComplexDown with the option
 * 6) assign the specific State corresponding to the function in {cz.cvut.fel.ida.neural.networks.structure.building.builders.StatesBuilder#getAggregationState} if created
 * 7) update dimensionality inference in {cz.cvut.fel.ida.neural.networks.structure.building.builders.StatesBuilder} if necessary
 *
 *
 * <p>
 * Created by gusta on 8.3.17.
 */
public abstract class ElementWise implements Transformation {
    private static final Logger LOG = Logger.getLogger(ElementWise.class.getName());

    /**
     * Forward-pass function
     */
    transient Function<Double, Double> evaluation;
    /**
     * Backward-pass / derivative of the evaluation function
     */
    transient Function<Double, Double> gradient;


    protected ElementWise(Function<Double, Double> evaluation, Function<Double, Double> gradient) {
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
            case IDENTITY:
                return Singletons.identity;
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
        public static Identity identity = new Identity();
        public static Tanh tanh = new Tanh();
        public static Exponentiation exponentiation = new Exponentiation();
        public static SquareRoot sqrt = new SquareRoot();
        public static Inverse inverse = new Inverse();
        public static Reverse reverse = new Reverse();
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
            processedGradient = topGradient.elementTimes(inputFcnDerivative);       //elementTimes here - since the fcn to be differentiated was applied element-wise on a vector
        }
    }
}