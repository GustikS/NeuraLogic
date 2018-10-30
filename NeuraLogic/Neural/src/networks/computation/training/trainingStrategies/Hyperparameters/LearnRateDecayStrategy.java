package networks.computation.training.trainingStrategies.Hyperparameters;

import java.util.logging.Logger;

public abstract class LearnRateDecayStrategy {
    private static final Logger LOG = Logger.getLogger(LearnRateDecayStrategy.class.getName());

    double initialLearningRate;
    double actualLearningRate;

    int decays = 0;

    public LearnRateDecayStrategy(double initialLearningRate) {
        this.initialLearningRate = initialLearningRate;
        this.actualLearningRate = initialLearningRate;
    }

    public abstract double decay();
}