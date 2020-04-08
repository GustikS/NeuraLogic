package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

public interface ValueInitializer extends Exportable {

    void initScalar(ScalarValue scalar);

    void initVector(VectorValue vector);

    void initMatrix(MatrixValue matrix);

    static ValueInitializer getInitializer(Settings settings) {
        if (settings.initializer == Settings.InitSet.GLOROT) {
            return new GlorotUniformInitializer(settings);
        } else if (settings.initializer == Settings.InitSet.HE) {
            return new HeUniformInitializer(settings);
        } else if (settings.initializer == Settings.InitSet.SIMPLE)
            return new SimpleInitializer(settings);
        else
            return new SimpleInitializer(settings);
        //todo some other initializers, e.g. where the individual element values are not drawn as i.i.d.
    }
}
