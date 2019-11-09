package networks.computation.evaluation.values.distributions;

import settings.Settings;

import java.util.Random;

public abstract class Distribution {

    Random rg;
    double scale;

    public Distribution(Random rg, Settings settings) {
        this.rg = rg;
        this.scale = settings.randomInitScale;
    }


    public static Distribution getDistribution(Settings settings) {
        return new Uniform(settings.random, settings);
    }

    abstract double getDoubleValue();
}
