package cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class ArithmeticDecay extends LearnRateDecayStrategy {   //todo next crete exponential one...should be much better
    private static final Logger LOG = Logger.getLogger(ArithmeticDecay.class.getName());

    ScalarValue diff = new ScalarValue(0);

    public ArithmeticDecay(ScalarValue initialLearningRate, int epochCount) {
        super(initialLearningRate);
        diff = (ScalarValue) initialLearningRate.times(new ScalarValue(-1.0 / epochCount));
    }

    @Override
    public void decay(int epochNumber) {
        decays++;
        actualLearningRate.incrementBy(diff);
    }
}