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

    static ValueInitializer getInitializer(Settings settings) {
        if (settings.initializer == Settings.InitSet.GLOROT) {
            return new GlorotInitializer(settings);
        } else if (settings.initializer == Settings.InitSet.SIMPLE)
            return new SimpleInitializer(settings);
        else
            return new SimpleInitializer(settings);
        //todo some other initializers, e.g. where the individual element values are not drawn as i.i.d.
    }
}
