package cz.cvut.fel.ida.algebra.values.distributions;

import cz.cvut.fel.ida.setup.Settings;

import java.util.Random;
import java.util.logging.Logger;

public class Constant extends Distribution {
    private static final Logger LOG = Logger.getLogger(Constant.class.getName());

    public Constant(Random rg, Settings settings) {
        super(rg, settings);
    }

    public final double getDoubleValue() {
        return setting.constantInitValue;
    }

}