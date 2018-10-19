package networks.structure.weights;

import networks.computation.values.Value;

import java.util.logging.Logger;

/**
 * maybe todo - micro-optimization class when weight is just scalar - saves one pointer
 */
public class ScalarWeight extends Weight {
    private static final Logger LOG = Logger.getLogger(ScalarWeight.class.getName());

    double value;

    public ScalarWeight(String name, Value value, boolean fixed) {
        super(index, name, value, fixed);
    }
}
