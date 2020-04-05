package cz.cvut.fel.ida.algebra.values.distributions;

import cz.cvut.fel.ida.setup.Settings;

import java.util.Random;
import java.util.logging.Logger;

public class Normal extends Distribution{
    private static final Logger LOG = Logger.getLogger(Normal.class.getName());

    public Normal(Random rg, Settings settings) {
        super(rg, settings);
    }

    @Override
    public double getDoubleValue() {
        return rg.nextGaussian()*scale;
    }
}