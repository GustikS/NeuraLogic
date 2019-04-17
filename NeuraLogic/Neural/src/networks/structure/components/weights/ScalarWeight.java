package networks.structure.components.weights;

import networks.computation.evaluation.values.Value;

import java.util.logging.Logger;

/**
 * maybe todo - micro-optimization class when weight is just scalar - saves one pointer
 */
@Deprecated
public class ScalarWeight extends Weight {
    private static final Logger LOG = Logger.getLogger(ScalarWeight.class.getName());

    double value;

    public ScalarWeight(String name, Value value, boolean fixed) {
        super(0, name, value, fixed, false);
    }
}
