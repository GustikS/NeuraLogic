package networks.computation.evaluation.values.distributions;

import networks.computation.evaluation.values.MatrixValue;
import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.VectorValue;
import networks.structure.components.weights.Weight;
import settings.Settings;

import java.util.logging.Logger;

public class SimpleInitializer implements ValueInitializer {
    private static final Logger LOG = Logger.getLogger(SimpleInitializer.class.getName());

    Distribution distribution;

    public SimpleInitializer(Settings settings) {
        this.distribution = Distribution.getDistribution(settings);
    }

    @Override
    public void initWeight(Weight weight){
        weight.value.initialize(this);
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
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.cols; j++) {
                matrix.values[i][j] = distribution.getDoubleValue();
            }
        }
    }
}