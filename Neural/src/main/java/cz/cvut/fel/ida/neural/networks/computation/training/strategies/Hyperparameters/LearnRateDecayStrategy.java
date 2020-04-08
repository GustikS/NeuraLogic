package cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.logging.Logger;

public abstract class LearnRateDecayStrategy implements Exportable {
    private static final Logger LOG = Logger.getLogger(LearnRateDecayStrategy.class.getName());

    transient Settings settings;

    ScalarValue initialLearningRate;
    ScalarValue actualLearningRate;

    int decays = 0;

    public LearnRateDecayStrategy(Settings settings, ScalarValue initialLearningRate) {
        this.settings = settings;
        this.initialLearningRate = (ScalarValue) initialLearningRate.clone();
        this.actualLearningRate = initialLearningRate;
    }

    public abstract void decay(int epochNumber);

    public static LearnRateDecayStrategy getFrom(Settings settings, ScalarValue learningRate) {
        switch (settings.decaySet) {
            case ARITHMETIC:
                return new ArithmeticDecay(settings, learningRate);
            case GEOMETRIC:
                return new GeometricDecay(settings, learningRate);
            default:
                LOG.severe("Unknown learning rate decay strategy");
                return new GeometricDecay(settings, learningRate);
        }

    }
}