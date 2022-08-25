package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.algebra.values.distributions.Distribution;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class SimpleInitializer implements ValueInitializer {
    //todo do w.r.t. https://mmuratarat.github.io/2019-02-25/xavier-glorot-he-weight-init

    private static final Logger LOG = Logger.getLogger(SimpleInitializer.class.getName());

    Distribution distribution;

    public SimpleInitializer(Settings settings) {
        this.distribution = Distribution.getDistribution(settings);
    }

    @Override
    public void initScalar(ScalarValue scalar){
        scalar.value = distribution.getDoubleValue();
    }

    @Override
    public void initVector(VectorValue vector){
        for (int i = 0; i < vector.values.length; i++) { //hope JIT will optimize this access to length
            vector.values[i] = distribution.getDoubleValue();
        }
    }

    @Override
    public void initMatrix(MatrixValue matrix){
        final double[] values = matrix.values;

        for (int i = 0; i < values.length; i++) {
            values[i] = distribution.getDoubleValue();
        }
    }
}