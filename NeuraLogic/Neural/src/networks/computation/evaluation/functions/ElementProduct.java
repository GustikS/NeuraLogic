package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Element-wise dimension-aligned (all inputs must have equal dimensions) application of (sum-based) activation
 */
public class ElementProduct extends Aggregation {
    private static final Logger LOG = Logger.getLogger(ElementProduct.class.getName());

    Activation activation;

    public ElementProduct(Activation activation) {
        this.activation = activation;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value sum = sumInputs(inputs);
        return activation.evaluate(sum);
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        Value sum = sumInputs(inputs);
        return activation.differentiate(sum);
    }

    private Value sumInputs(List<Value> inputs){
        int[] size = inputs.get(0).size();
        for (int i = 0; i < inputs.size(); i++) {
            if (!Arrays.equals(size,inputs.get(i).size())) {
                LOG.severe("ScalarProduct dimensions mismatch!");
                return null;
            }
        }
        Value sum = inputs.get(0).clone().zero();
        for (Value input : inputs) {
            //"Scalar" element-wise aligned summation
            sum.increment(input);
        }
        return sum;
    }
}
