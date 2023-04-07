package cz.cvut.fel.ida.algebra.values.distributions;

import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.Random;
import java.util.logging.Logger;

public abstract class Distribution implements Exportable {
    private static final Logger LOG = Logger.getLogger(Distribution.class.getName());

    transient Settings setting;
    transient Random rg;
    /**
     * this is either limit for uniform or STD for normal
     */
    double scale;

    public Distribution(Random rg, Settings settings) {
        this.rg = rg;
        this.scale = settings.randomInitScale;
        this.setting = settings;
    }


    public static Distribution getDistribution(Settings settings) {
        if (settings.initDistribution == Settings.InitDistribution.UNIFORM)
            return new Uniform(settings.random, settings);
        else if (settings.initDistribution == Settings.InitDistribution.NORMAL) {
            return new Normal(settings.random, settings);
        } else if (settings.initDistribution == Settings.InitDistribution.CONSTANT) {
            return new Constant(settings.random, settings);
        }
        else if (settings.initDistribution == Settings.InitDistribution.LONGTAIL) {
            return new Longtail(settings.random, settings);
        }
        LOG.warning("Wrong weights initialization setup, choosing default Uniform distribution");
        return new Uniform(settings.random, settings);  //default
    }

    public abstract double getDoubleValue();
}
