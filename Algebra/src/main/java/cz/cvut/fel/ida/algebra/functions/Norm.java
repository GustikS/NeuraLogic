package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

public class Norm extends Activation {
    private static final Logger LOG = Logger.getLogger(Norm.class.getName());


    public Norm() {
        super(null, null);
    }

    @Override
    public boolean isComplex() {
        return false;
    }

    @Override
    public Aggregation replaceWithSingleton() {
        return Singletons.norm;
    }

    public Value evaluate(Value summedInputs) {
//        double len = getSum(summedInputs);
        double len = summedInputs.size()[0] * summedInputs.size()[1];
//        double norm = Math.sqrt(len);
//        return summedInputs.apply(x -> x / norm);
        return new ScalarValue(len);
    }

    /**
     * constant identity gradient 1.0 of the same dimensionality
     *
     * @param summedInputs
     * @return
     */
    public Value differentiate(Value summedInputs) {
//        double len = getSum(summedInputs);
//        double len = summedInputs.size()[0] * summedInputs.size()[1];
//        double norm = 1 / Math.sqrt(len);
//        Value form = summedInputs.getForm();
//        return form.apply(in -> norm);
        LOG.warning("Propagating gradient through a NORM fcn");
        return new ScalarValue(0.0);    //todo check
    }

    public double getSum(Value summedInputs) {
        double sum = 0;
        for (Double summedInput : summedInputs) {
            sum += summedInput;
        }
        return sum;
    }
}
