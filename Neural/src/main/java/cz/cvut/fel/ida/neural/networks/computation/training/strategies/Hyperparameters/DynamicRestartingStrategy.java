package cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters;

import cz.cvut.fel.ida.learning.results.Progress;

import java.util.logging.Logger;

/**
 * todo restart after stagnation of error for long enough, or progresively increasing number of epochae
 */
public class DynamicRestartingStrategy extends RestartingStrategy {
    private static final Logger LOG = Logger.getLogger(DynamicRestartingStrategy.class.getName());
    private int maxSteps;

    public DynamicRestartingStrategy(int maxSteps){
        this.maxSteps = maxSteps;
    }

    @Override
    public boolean continueRestart(Progress progress) {
        if (progress.getEpochCount() > maxSteps){
            return false;
        }
        return true;
    }

    @Override
    public void nextRestart() {

    }
}
