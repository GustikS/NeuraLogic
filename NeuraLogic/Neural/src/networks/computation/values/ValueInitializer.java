package networks.computation.values;

import networks.structure.weights.Weight;
import settings.Settings;

import java.util.Random;
import java.util.logging.Logger;

public class ValueInitializer {
    private static final Logger LOG = Logger.getLogger(ValueInitializer.class.getName());
    Random rg;

    public ValueInitializer(Settings settings) {
        this.rg = settings.random;
    }

    public void initWeight(Weight weight){
        weight.value.initialize(this);
    }

    public void initScalar(ScalarValue scalar){
        scalar.value = getDoubleValue();
    }

    public void initVector(VectorValue vector){
        for (int i = 0; i < vector.value.length; i++) { //hope JIT will optimize this access to length
            vector.value[i] = getDoubleValue();
        }
    }

    public void initMatrix(MatrixValue matrix){
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.cols; j++) {
                matrix.value[i][j] = getDoubleValue();
            }
        }
    }

    private final double getDoubleValue(){
        return rg.nextDouble() - 0.5; //TODO consider other distributions as per settings
    }
}