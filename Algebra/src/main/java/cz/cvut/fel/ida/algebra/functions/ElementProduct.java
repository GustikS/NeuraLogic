package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

/**
 * Element-wise dimension-aligned (all inputs must have equal dimensions) application of (sum-based) activation.
 * Essentially this is just a summation aggregation when properly dimensioned inputs are provided.
 */
public class ElementProduct extends Activation {
    private static final Logger LOG = Logger.getLogger(ElementProduct.class.getName());

    @Override
    public String getName() {
        return "DotProduct";
    }

    Activation activation;

    public ElementProduct(Activation activation) {
        super(activation.evaluation, activation.gradient);
        this.activation = activation;
    }

    @Override
    public Aggregation replaceWithSingleton() {
        LOG.severe("ElementProduct cannot be singleton.");
        return null;
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
        /*
        for (int i = 0; i < inputs.size(); i++) {
            if (!Arrays.equals(size,inputs.get(i).size())) {
                LOG.severe("ScalarProduct dimensions mismatch!");   //get maximal dimension here instead (zero-pad the rest) ? -> no, invalid vector/matrix operation anyway, rather do not misuse it
                return null;
            }
        }
        */
        Value sum = inputs.get(0).getForm();
        for (Value input : inputs) {
            //"Scalar" element-wise aligned summation
            sum.incrementBy(input);
        }
        return sum;
    }
}
