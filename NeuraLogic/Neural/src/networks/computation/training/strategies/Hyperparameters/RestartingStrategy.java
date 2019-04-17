package networks.computation.training.strategies.Hyperparameters;

import networks.computation.evaluation.results.Progress;
import settings.Settings;

import java.util.logging.Logger;

public abstract class RestartingStrategy {
    private static final Logger LOG = Logger.getLogger(RestartingStrategy.class.getName());

    public RestartingStrategy(){

    }

    public static RestartingStrategy getFrom(Settings settings) {
        return new StaticRestartingStrategy(1000);
    }

    public abstract boolean continueRestart(Progress progress);
}