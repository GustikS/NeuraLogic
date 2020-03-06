package evaluation.values.distributions;

import settings.Settings;

import java.util.Random;
import java.util.logging.Logger;

public class Uniform extends Distribution {
    private static final Logger LOG = Logger.getLogger(Uniform.class.getName());

    public Uniform(Random rg, Settings settings) {
        super(rg, settings);
    }

    final double getDoubleValue() {
        return (rg.nextDouble() - 0.5) * scale;
    }

    final double getDoubleValue(double min, double max) {
        double scale = max - min;
        double v = rg.nextDouble() * scale + min;
        return v;
    }
}