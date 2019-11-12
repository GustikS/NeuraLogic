package networks.computation.evaluation.values.distributions;

import settings.Settings;

import java.util.Random;
import java.util.logging.Logger;

public abstract class Distribution {
    private static final Logger LOG = Logger.getLogger(Distribution.class.getName());

    Settings setting;
    Random rg;
    double scale;

    public Distribution(Random rg, Settings settings) {
        this.rg = rg;
        this.scale = settings.randomInitScale;
        this.setting = settings;
    }


    public static Distribution getDistribution(Settings settings) {
        if (settings.initDistribution == Settings.InitDistribution.UNIFORM)
            return new Uniform(settings.random, settings);
        else if (settings.initDistribution == Settings.InitDistribution.CONSTANT){
            return new Constant(settings.random, settings);
        }
        LOG.warning("Wrong weights initialization setup, choosing default distribution");
        return new Uniform(settings.random, settings);  //default
    }

    abstract double getDoubleValue();
}
