package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.algebra.values.distributions.Normal;
import cz.cvut.fel.ida.algebra.values.distributions.Uniform;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class GlorotUniformInitializer implements ValueInitializer {
    private static final Logger LOG = Logger.getLogger(GlorotUniformInitializer.class.getName());

    Uniform distribution;
    private final Normal gaussian;

    protected final Settings settings;

    /** The activation correction, folded into every limit below. 1 until asked for otherwise. */
    protected double gain = ActivationGain.LINEAR;

    /**
     * Orthonormal rows instead of independent draws, for a weight a recursive rule applies over and over.
     * Only ever set from {@link Weight#onRecurrentRule}; it is not something to pick.
     */
    protected boolean orthogonal = false;

    /** Overridden by the subclasses so that configuring one hands back its own kind rather than this one. */
    protected GlorotUniformInitializer copy() {
        return new GlorotUniformInitializer(this.settings);
    }

    private ValueInitializer configured(double gain, boolean orthogonal) {
        if (gain == this.gain && orthogonal == this.orthogonal) {
            return this;
        }
        GlorotUniformInitializer sibling = copy();
        sibling.gain = gain;
        sibling.orthogonal = orthogonal;
        return sibling;
    }

    @Override
    public ValueInitializer withGain(double gain) {
        return configured(gain, this.orthogonal);
    }

    @Override
    public ValueInitializer forWeight(Weight weight) {
        return configured(weight.activationGain, weight.onRecurrentRule);
    }

    public GlorotUniformInitializer(Settings settings) {
        this.settings = settings;
        this.distribution = new Uniform(settings.random, settings);
        this.gaussian = new Normal(settings.random, settings);
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
        if (orthogonal && matrix.rows == matrix.cols) {
            //square only: orthonormal rows replace the fan scaling rather than combining with it, and on a
            //rectangular weight the two would be answering different questions
            Orthonormalisation.fill(matrix, gaussian, gain);
            return;
        }
        final double limit = getLimit(matrix);
        final double[] values = matrix.values;

        for (int i = 0; i < values.length; i++) {
            values[i] = distribution.getDoubleValue(-limit, limit);
        }
    }

    protected double getLimit(MatrixValue value) {
        return gain * Math.sqrt(6) / Math.sqrt(value.cols + value.rows);
    }

    protected double getLimit(VectorValue value) {
        return gain * Math.sqrt(6) / Math.sqrt(value.values.length + 1);
    }

    protected double getLimit(ScalarValue value) {
        return gain * Math.sqrt(6) / Math.sqrt(1 + 1);
    }
}