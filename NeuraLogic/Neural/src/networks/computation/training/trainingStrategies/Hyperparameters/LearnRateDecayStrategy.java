package networks.computation.training.trainingStrategies.Hyperparameters;

import settings.Settings;

import java.util.logging.Logger;

public abstract class LearnRateDecayStrategy {
    private static final Logger LOG = Logger.getLogger(LearnRateDecayStrategy.class.getName());

    Settings settings;

    double initialLearningRate;
    double actualLearningRate;

    int decays = 0;

    public LearnRateDecayStrategy(Settings settings, double initialLearningRate) {
        this.settings = settings;
        this.initialLearningRate = initialLearningRate;
        this.actualLearningRate = initialLearningRate;
    }

    public abstract double decay();

    public static LearnRateDecayStrategy getFrom(Settings settings, double learningRate) {
        return new LinearDecay(settings, learningRate);    //todo rest
    }
}