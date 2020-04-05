package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class HeUniformInitializer extends GlorotUniformInitializer {  //sqrt(2. / (in)) for normal distribution
    private static final Logger LOG = Logger.getLogger(HeUniformInitializer.class.getName());

    public HeUniformInitializer(Settings settings) {
        super(settings);
    }

    protected double getLimit(MatrixValue value) {
        return Math.sqrt(6) / Math.sqrt(value.cols);
    }

    protected double getLimit(VectorValue value) {
        return Math.sqrt(6) / Math.sqrt(value.values.length); //todo check this should be the fan_in (input) dimension
    }

    protected double getLimit(ScalarValue value) {
        return Math.sqrt(6) / Math.sqrt(1);
    }

}
