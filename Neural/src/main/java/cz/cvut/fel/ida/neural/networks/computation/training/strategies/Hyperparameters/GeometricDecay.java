package cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class GeometricDecay extends LearnRateDecayStrategy {
    private static final Logger LOG = Logger.getLogger(GeometricDecay.class.getName());

    Double ratio;
    int everyNepocha;

    public GeometricDecay(Settings settings, ScalarValue initialLearningRate) {
        super(settings, initialLearningRate);
        this.ratio = settings.learnRateDecay;
        this.everyNepocha = settings.decaySteps;
    }

    @Override
    public void decay(int epochNumber) {
        if (epochNumber > 0 && epochNumber % everyNepocha == 0) {
            actualLearningRate.value *= ratio;
            LOG.fine("Decayed learning rate from initial " + initialLearningRate.toString(Settings.detailedNumberFormat) + " to " + actualLearningRate.toString(Settings.detailedNumberFormat));
            decays++;
        }
    }
}
