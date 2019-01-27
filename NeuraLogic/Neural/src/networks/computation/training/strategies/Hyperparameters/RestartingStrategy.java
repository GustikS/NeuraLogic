package networks.computation.training.strategies.Hyperparameters;

import networks.computation.evaluation.results.Progress;

import java.util.logging.Logger;

public abstract class RestartingStrategy {
    private static final Logger LOG = Logger.getLogger(RestartingStrategy.class.getName());

    public RestartingStrategy(Progress progress){

    }

    public abstract boolean continueRestart();


}
