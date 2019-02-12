package networks.computation.evaluation.values.distributions;

import settings.Settings;

import java.util.Random;

public abstract class Distribution {

    Random rg;

    public Distribution(Random rg){
        this.rg = rg;
    }

    public static Distribution getDistribution(Settings settings) {
        return new Uniform(settings.random);
    }

    abstract double getDoubleValue();
}
