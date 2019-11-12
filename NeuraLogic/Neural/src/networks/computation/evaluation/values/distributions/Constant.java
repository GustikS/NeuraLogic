package networks.computation.evaluation.values.distributions;

import settings.Settings;

import java.util.Random;
import java.util.logging.Logger;

public class Constant extends Distribution {
    private static final Logger LOG = Logger.getLogger(Constant.class.getName());

    public Constant(Random rg, Settings settings) {
        super(rg, settings);
    }

    final double getDoubleValue() {
        return setting.constantInitValue;
    }

}