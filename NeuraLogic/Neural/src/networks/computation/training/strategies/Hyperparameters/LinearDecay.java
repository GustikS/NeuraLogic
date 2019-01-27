package networks.computation.training.strategies.Hyperparameters;

import settings.Settings;

import java.util.logging.Logger;

public class LinearDecay extends LearnRateDecayStrategy {
    private static final Logger LOG = Logger.getLogger(LinearDecay.class.getName());

    double diff = 0;

    public LinearDecay(Settings settings, double initialLearningRate) {
        super(settings, initialLearningRate);
        diff = initialLearningRate / settings.maxCumEpochCount;
    }

    @Override
    public double decay() {
        decays++;
        return actualLearningRate - diff;
    }
}
