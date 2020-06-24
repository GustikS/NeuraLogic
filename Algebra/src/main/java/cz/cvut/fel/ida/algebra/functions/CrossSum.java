package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Takes all combinations of dimensions of all inputs -> sum into a long vector -> activation
 * This is a possible rule neuron's activation function!
 * <p>
 * todo if too slow in evaluation, precalculate the final vector via pointers to the underyling values via special aggregationState?
 */
public class CrossSum extends Activation {
    private static final Logger LOG = Logger.getLogger(CrossSum.class.getName());

    Activation activation;

    @Override
    public Aggregation replaceWithSingleton() {
        LOG.severe("CrossSum cannot be singleton (may have different Activations)");
        return null;
    }

    public CrossSum(Activation activation) {
        super(activation.evaluation, activation.gradient);
        this.activation = activation;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public boolean isInputSymmetric() {
        return false;   // changing the number of inputs in crossproduct is problematic...
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        List<Value> values = new ArrayList<>(inputs.size());
        values.addAll(inputs);
        List<Double> outputVector = new ArrayList<>();
        combinationsRecursive(outputVector, 0.0, values);
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
     *
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
