package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;

import java.util.List;
import java.util.function.Function;

/**
 * Class representing activation functions, i.e. those that firstly accumulate input values via summation and then apply some non-linearity on top.
 * The non-linearity is provided via FunctionalInterface Function separately for evaluation and derivative/gradient.
 * <p>
 * Created by gusta on 8.3.17.
 */
public abstract class Activation extends Aggregation {

    /**
     * Forward-pass function
     */
    Function<Double, Double> evaluation;
    /**
     * Backward-pass / derivative of the evaluation function
     */
    Function<Double, Double> gradient;

    public Activation(Function<Double, Double> evaluation, Function<Double, Double> gradient) {
        this.evaluation = evaluation;
        this.gradient = gradient;
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
}