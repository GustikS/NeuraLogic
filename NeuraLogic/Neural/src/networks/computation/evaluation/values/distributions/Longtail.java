package networks.computation.evaluation.values.distributions;

import settings.Settings;

import java.util.Random;
import java.util.logging.Logger;

public class Longtail extends Distribution {
    private static final Logger LOG = Logger.getLogger(Longtail.class.getName());

    public Longtail(Random rg, Settings settings) {
        super(rg, settings);
    }

    final double getDoubleValue() {
        double power = 50;
        double x0 = 0;
        double x1 = 10;
        double y = rg.nextDouble();
        double x = x1 - (Math.pow(((Math.pow(x1, (power + 1)) - Math.pow(x0, (power + 1))) * y + Math.pow(x0, (power + 1))), (1 / (power + 1))));
        return x;
    }

}