package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

public interface ValueInitializer extends Exportable {

    void initScalar(ScalarValue scalar);

    void initVector(VectorValue vector);

    void initMatrix(MatrixValue matrix);

    /**
     * An initializer for the same rule but widened by the activation a weight's output passes through - see
     * {@link ActivationGain}. Only the ones that already scale with the weight's shape answer this; the rest
     * return themselves, since {@link Settings.InitSet#SIMPLE} means a distribution the user named outright
     * and correcting it would be answering a question nobody asked.
     */
    default ValueInitializer withGain(double gain) {
        return this;
    }

    /**
     * An initializer set up for one weight's hints - the activation its output meets, and whether a
     * recursive rule applies it. The ones that scale with shape answer this; the rest hand back themselves.
     */
    default ValueInitializer forWeight(Weight weight) {
        return this;
    }

    static ValueInitializer getInitializer(Settings settings) {
        if (settings.initializer == Settings.InitSet.TORCH) {
            return new TorchUniformInitializer(settings);
        } else if (settings.initializer == Settings.InitSet.GLOROT) {
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
