package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

public class NormDiv extends Activation {
    private static final Logger LOG = Logger.getLogger(NormDiv.class.getName());


    public NormDiv() {
        super(null, null);
    }

    @Override
    public boolean isComplex() {
        return false;
    }

    @Override
    public Aggregation replaceWithSingleton() {
        return Singletons.normDiv;
    }

    public Value evaluate(Value summedInputs) {
        double sum = getSum(summedInputs);
        double norm = Math.sqrt(sum);
        return summedInputs.apply(x -> x / norm);
    }

    /**
     * constant identity gradient 1.0 of the same dimensionality
     *
     * @param summedInputs
     * @return
     */
    public Value differentiate(Value summedInputs) {
        double sum = getSum(summedInputs);
        double norm = 1 / Math.sqrt(sum);
        Value form = summedInputs.getForm();
        return form.apply(in -> norm);
    }

    public double getSum(Value summedInputs) {
        double sum = 0;
        for (Double summedInput : summedInputs) {
            sum += summedInput;
        }
        return sum;
    }
}
