package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;
import networks.computation.evaluation.values.VectorValue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Takes all combinations of dimensions of all inputs -> sum into a long vector -> activation
 */
public class CrossProduct extends Aggregation {
    private static final Logger LOG = Logger.getLogger(CrossProduct.class.getName());

    Activation activation;

    public CrossProduct(Activation activation) {
        this.activation = activation;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        List<Value> clone = new ArrayList<>(inputs.size());
        clone.addAll(inputs);
        List<Double> outputVector = new ArrayList<>();
        combinationsRecursive(outputVector, 0.0, clone);
        return activation.evaluate(new VectorValue(outputVector));
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        List<Value> clone = new ArrayList<>(inputs.size());
        clone.addAll(inputs);
        List<Double> outputVector = new ArrayList<>();
        combinationsRecursive(outputVector, 0.0, clone);
        return activation.differentiate(new VectorValue(outputVector));
    }


    /**
     * All combinations of dimensions of all inputs -> long vector
     * @param output
     * @param sum
     * @param values
     */
    private void combinationsRecursive(List<Double> output, double sum, List<Value> values) {
        if (values.size() == 0) {
            output.add(sum);
            return;
        }
        Value removed = values.remove(0);
        for (Double next : removed) {
            sum += next;
            combinationsRecursive(output, sum, values);
            sum -= next;
        }
        values.add(0, removed);
    }
}
