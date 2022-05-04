package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * It flattens/linearizes the input Values, hence the result is always a VectorValue!
 *  - to avoid the necessity to specify along which dimension to do the concat...       //todo make a parameterized version
 */
public class Concatenation extends Combination {
    private static final Logger LOG = Logger.getLogger(Concatenation.class.getName());

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public boolean isInputSymmetric() {
        return false;
    }

    @Override
    public Aggregation replaceWithSingleton() {
        return Singletons.crossSum;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        return concatenate(inputs);
    }

    public static VectorValue concatenate(List<Value> inputs) {
        List<Double> concat = new ArrayList<>();
        for (Value input : inputs) {
            for (Double val : input) {
                concat.add(val);
            }
        }
        return new VectorValue(concat);
    }
}
