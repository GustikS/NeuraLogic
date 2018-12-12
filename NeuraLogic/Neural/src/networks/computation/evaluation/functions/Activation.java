package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;

import java.util.List;
import java.util.function.Function;

/**
 * Class representing activation functions, i.e. those that firstly acummulate input values via summation and then apply some nonlinearity on top.
 * <p>
 * Created by gusta on 8.3.17.
 */
public abstract class Activation extends Aggregation {

    Function<Double, Double> evaluation;
    Function<Double, Double> gradient;

    public Activation(Function<Double, Double> evaluation, Function<Double, Double> gradient) {
        this.evaluation = evaluation;
        this.gradient = gradient;
    }

    Value evaluate(Value summedInputs) {
        return summedInputs.apply(evaluation);
    }

    Value differentiate(Value summedInputs) {
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