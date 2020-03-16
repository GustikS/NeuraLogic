package cz.cvut.fel.ida.algebra.values.distributions;

import cz.cvut.fel.ida.setup.Settings;

import java.util.Random;
import java.util.logging.Logger;

public class Uniform extends Distribution {
    private static final Logger LOG = Logger.getLogger(Uniform.class.getName());

    public Uniform(Random rg, Settings settings) {
        super(rg, settings);
    }

    public final double getDoubleValue() {
        return (rg.nextDouble() - 0.5) * scale;
    }

    public final double getDoubleValue(double min, double max) {
        double scale = max - min;
        double v = rg.nextDouble() * scale + min;
        return v;
    }
}