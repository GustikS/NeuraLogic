package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

/**
 * Returns size of the input tensor (length for vector, number of cells for matrix)
 */
public class Size extends Transformation {
    private static final Logger LOG = Logger.getLogger(Size.class.getName());


    @Override
    public boolean isComplex() {
        return false;
    }

    @Override
    public Aggregation replaceWithSingleton() {
        return Singletons.size;
    }

    public Value evaluate(Value combinedInputs) {
//        double len = getSum(summedInputs);
        double len = 1;

        for (int i : combinedInputs.size()) {
            len *= i;
        }
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
        return Value.ZERO;    //todo check
    }

    public double getSum(Value summedInputs) {
        double sum = 0;
        for (Double summedInput : summedInputs) {
            sum += summedInput;
        }
        return sum;
    }
}
