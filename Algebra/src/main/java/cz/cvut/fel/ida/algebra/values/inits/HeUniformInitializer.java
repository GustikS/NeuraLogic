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

    @Override
    public ValueInitializer withGain(double gain) {
        if (gain == this.gain) {
            return this;
        }
        HeUniformInitializer widened = new HeUniformInitializer(this.settings);
        widened.gain = gain;
        return widened;
    }

    protected double getLimit(MatrixValue value) {
        return gain * Math.sqrt(6) / Math.sqrt(value.cols);
    }

    protected double getLimit(VectorValue value) {
        if (value.rowOrientation) {
            return gain * Math.sqrt(6) / Math.sqrt(value.values.length);
        } else
            return gain * Math.sqrt(6) / 1; //todo check this should be the fan_in (input) dimension
    }

    protected double getLimit(ScalarValue value) {
        return gain * Math.sqrt(6) / Math.sqrt(1);
    }

}
