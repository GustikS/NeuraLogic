package networks.computation.evaluation.values.distributions;

import java.util.Random;
import java.util.logging.Logger;

public class Uniform extends Distribution {
    private static final Logger LOG = Logger.getLogger(Uniform.class.getName());

    public Uniform(Random rg) {
        super(rg);
    }

    final double getDoubleValue() {
        return rg.nextDouble() - 0.5;
    }

    final double getDoubleValue(double min, double max) {
        double scale = max - min;
        double v = rg.nextDouble() * scale + min;
        return v;
    }
}