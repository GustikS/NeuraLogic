package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;

import java.util.List;

/**
 * Class representing activation functions, i.e. those that firstly acummulate input values via summation and then apply some nonlinearity on top.
 *
 * Created by gusta on 8.3.17.
 */
public interface Activation extends Aggregation {

    Value evaluate(Value summedInputs);

    Value differentiate(Value summedInputs);

    @Override
    default Value evaluate(List<Value> inputs){
        Value sum = inputs.get(0).clone();
        for (int i = 1, len = inputs.size(); i < len; i++) {
            sum.plus(inputs.get(i));
        }
        return evaluate(sum);
    }

    @Override
    default Value differentiate(List<Value> inputs){
        Value sum = inputs.get(0).clone();
        for (int i = 1, len = inputs.size(); i < len; i++) {
            sum.plus(inputs.get(i));
        }
        return differentiate(sum);
    }
}