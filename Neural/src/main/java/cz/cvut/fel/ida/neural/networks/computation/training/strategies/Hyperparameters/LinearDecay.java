package cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class LinearDecay extends LearnRateDecayStrategy {   //todo next crete exponential one...should be much better
    private static final Logger LOG = Logger.getLogger(LinearDecay.class.getName());

    ScalarValue diff = new ScalarValue(0);

    public LinearDecay(Settings settings, ScalarValue initialLearningRate) {
        super(settings, initialLearningRate);
        diff = (ScalarValue) initialLearningRate.times(new ScalarValue(-1.0 / settings.maxCumEpochCount));
    }

    @Override
    public void decay() {
        decays++;
        actualLearningRate.incrementBy(diff);
    }
}
