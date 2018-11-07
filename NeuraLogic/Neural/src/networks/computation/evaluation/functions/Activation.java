package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;

import java.util.ArrayList;

/**
 * Class representing various activation and also aggregation functions, since there are the same requirements for both.
 * That is evaluation and differentiation.
 *
 * Created by gusta on 8.3.17.
 */
public interface Activation extends Aggregation {

    Value evaluate(Value summedInputs);

    default Value evaluate(ArrayList<Value> inputs){
        Value sum = inputs.get(0).clone();
        for (int i = 1, len = inputs.size(); i < len; i++) {
            sum.plus(inputs.get(i));
        }
        return evaluate(sum);
    }
}