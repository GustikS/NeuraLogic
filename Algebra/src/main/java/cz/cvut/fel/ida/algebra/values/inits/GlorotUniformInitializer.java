package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.algebra.values.distributions.Uniform;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class GlorotUniformInitializer implements ValueInitializer {
    private static final Logger LOG = Logger.getLogger(GlorotUniformInitializer.class.getName());

    Uniform distribution;

    public GlorotUniformInitializer(Settings settings) {
        this.distribution = new Uniform(settings.random, settings);
    }

//    public void initWeight(Weight weight) {
//        weight.value.initialize(this);
//    }

    @Override
    public void initScalar(ScalarValue scalar) {
        double limit = getLimit(scalar);
        scalar.value = distribution.getDoubleValue(-limit, limit);
    }

    @Override
    public void initVector(VectorValue vector) {
        double limit = getLimit(vector);
        for (int i = 0; i < vector.values.length; i++) { //hope JIT will optimize this access to length
            vector.values[i] = distribution.getDoubleValue(-limit, limit);
        }
    }

    @Override
    public void initMatrix(MatrixValue matrix) {
        final double limit = getLimit(matrix);
        final double[] values = matrix.values;

        for (int i = 0; i < values.length; i++) {
            values[i] = distribution.getDoubleValue(-limit, limit);
        }
    }

    protected double getLimit(MatrixValue value) {
        return Math.sqrt(6) / Math.sqrt(value.cols + value.rows);
    }

    protected double getLimit(VectorValue value) {
        return Math.sqrt(6) / Math.sqrt(value.values.length + 1);
    }

    protected double getLimit(ScalarValue value) {
        return Math.sqrt(6) / Math.sqrt(1 + 1);
    }
}