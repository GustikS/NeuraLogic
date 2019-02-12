package networks.computation.evaluation.values.distributions;

import networks.computation.evaluation.values.MatrixValue;
import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.VectorValue;
import networks.structure.components.weights.Weight;
import settings.Settings;

public interface ValueInitializer {
    void initWeight(Weight weight);

    void initScalar(ScalarValue scalar);

    void initVector(VectorValue vector);

    void initMatrix(MatrixValue matrix);

    default ValueInitializer getInitializer(Settings settings){
        return new SimpleInitializer(settings); //todo some other initializers, e.g. where the individual element values are not drawn as i.i.d.
    }
}
