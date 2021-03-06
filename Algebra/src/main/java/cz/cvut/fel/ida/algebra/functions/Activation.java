package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.specific.*;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Class representing activation functions, i.e. those that firstly accumulate input values via summation and then apply some non-linearity on top.
 * The non-linearity is provided via FunctionalInterface Function separately for evaluation and derivative/gradient.
 * <p>
 * Steps for adding new activation/aggregation:
 * 1) add definition of the function (evaluation+differentiation) by overriding {@link Activation} or {@link Aggregation} class
 * 2) create a static singleton in {@link Activation} or {@link Aggregation} for reuse, if possible
 * 3) update {@link Activation#parseActivation(String)} and {@link Activation#getActivationFunction(Settings.ActivationFcn)} with the new option
 * 4) if beneficial/required, create new computation state in {cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.AggregationState} for the function
 * 5) if the function requires special backprop treatment, create new Visitors.Down propagator and update ComplexDown with the option
 * 6) assign a specific State corresponding to the function in {cz.cvut.fel.ida.neural.networks.structure.building.builders.StatesBuilder#getAggregationState}
 * 7) update dimensionality inference in {cz.cvut.fel.ida.neural.networks.structure.building.builders.StatesBuilder} if necessary
 *
 *
 * <p>
 * Created by gusta on 8.3.17.
 */
public abstract class Activation extends Aggregation {
    private static final Logger LOG = Logger.getLogger(Activation.class.getName());

    /**
     * Forward-pass function
     */
    transient Function<Double, Double> evaluation;
    /**
     * Backward-pass / derivative of the evaluation function
     */
    transient Function<Double, Double> gradient;

    protected Activation(Function<Double, Double> evaluation, Function<Double, Double> gradient) {
        this.evaluation = evaluation;
        this.gradient = gradient;
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }

    public Value evaluate(Value summedInputs) {
        return summedInputs.apply(evaluation);
    }

    public Value differentiate(Value summedInputs) {
        return summedInputs.apply(gradient);
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value sum = inputs.get(0).clone();
        for (int i = 1, len = inputs.size(); i < len; i++) {
            sum.plus(inputs.get(i));
        }
        return evaluate(sum);
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        Value sum = inputs.get(0).clone();
        for (int i = 1, len = inputs.size(); i < len; i++) {
            sum.plus(inputs.get(i));
        }
        return differentiate(sum);
    }

    public static Activation getActivationFunction(Settings.ActivationFcn activationFcn) {
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
            case LUKASIEWICZ:
                return Singletons.lukasiewiczSigmoid;
            case SOFTMAX:
                return Singletons.softmax;
            case SPARSEMAX:
                return Singletons.sparsemax;
            default:
                LOG.severe("Unimplemented activation function");
                return null;
        }
    }

    public static Aggregation parseActivation(String agg) {
        switch (agg) {
            case "sigmoid":
                return Activation.Singletons.sigmoid;
            case "sigm":
                return Activation.Singletons.sigmoid;
            case "tanh":
                return Activation.Singletons.tanh;
            case "signum":
                return Activation.Singletons.signum;
            case "relu":
                return Activation.Singletons.relu;
            case "identity":
                return Activation.Singletons.identity;
            case "lukasiewicz":
                return Activation.Singletons.lukasiewiczSigmoid;
            case "softmax":
                return Activation.Singletons.softmax;
            case "sparsemax":
                return Activation.Singletons.sparsemax;
            default:
                if (agg.startsWith("crosssum-")) {
                    String inner = agg.substring(agg.indexOf("-") + 1);
                    Aggregation innerActivation = parseActivation(inner);
                    return new CrossSum((Activation) innerActivation);
                } else if (agg.startsWith("elementproduct-")) {
                    String inner = agg.substring(agg.indexOf("-") + 1);
                    Aggregation innerActivation = parseActivation(inner);
                    return new ElementProduct((Activation) innerActivation);
                } else if (agg.startsWith("product-")) {
                    String inner = agg.substring(agg.indexOf("-") + 1);
                    Aggregation innerActivation = parseActivation(inner);
                    return new Product((Activation) innerActivation);
                } else if (agg.startsWith("concat-")) {
                    String inner = agg.substring(agg.indexOf("-") + 1);
                    Aggregation innerActivation = parseActivation(inner);
                    return new Concatenation((Activation) innerActivation);
                }
                throw new RuntimeException("Unable to parse activation function: " + agg);
        }
    }

    public static class Singletons {
        public static LukasiewiczSigmoid lukasiewiczSigmoid = new LukasiewiczSigmoid();
        public static Sigmoid sigmoid = new Sigmoid();
        public static Signum signum = new Signum();
        public static ReLu relu = new ReLu();
        public static Identity identity = new Identity();
        public static Tanh tanh = new Tanh();
        public static Softmax softmax = new Softmax();
        public static Sparsemax sparsemax = new Sparsemax();
    }
}