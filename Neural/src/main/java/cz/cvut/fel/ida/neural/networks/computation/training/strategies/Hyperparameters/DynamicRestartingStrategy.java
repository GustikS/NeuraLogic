package cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

/**
 * Restart after stagnation of (train/val) error for long enough.
 * Based on moving averages of the respective error within two consecutive time-spans
 */
public class DynamicRestartingStrategy extends RestartingStrategy {
    private static final Logger LOG = Logger.getLogger(DynamicRestartingStrategy.class.getName());

    Settings.DataSelection dataSelection;
    int timeSpan = 100;
    int recalculation = 10;

    Value minDelta = new ScalarValue(0.0001);

    Value avgLossPast;
    Value avgLossPresent;

    Value pastLoss0 = new ScalarValue(0);
    Value pastLoss1 = new ScalarValue(0);

    Value minusOne = new ScalarValue(-1);

    public DynamicRestartingStrategy(Settings settings, boolean validationPossible) {
        timeSpan = settings.earlyStoppingPatience;
        recalculation = settings.resultsRecalculationEpochae;
        if (!validationPossible){
            dataSelection = Settings.DataSelection.ONLINETRAIN;
        } else {
            dataSelection = settings.dataSelection;
        }
    }

    @Override
    public boolean continueRestart(Progress progress) {
        if (progress.getEpochCount() == 0) {
            return true;
        }
        if (progress.getEpochCount() == 1) {
            avgLossPresent = progress.getCurrentOnlineTrainingResults().error.clone();
            avgLossPast = progress.getCurrentOnlineTrainingResults().error.clone();
        }
        Value presentLoss = getLoss(progress.currentRestart, 0);

        if (progress.getEpochCount() < timeSpan) {
            avgLossPresent.incrementBy(presentLoss);
            return true;
        } else if (progress.getEpochCount() < 2 * timeSpan) {
            avgLossPresent.incrementBy(presentLoss);
            pastLoss1 = getLoss(progress.currentRestart, timeSpan - 1);
            avgLossPast.incrementBy(pastLoss1);
            return true;
        } else {
            pastLoss0 = getLoss(progress.currentRestart, 2 * timeSpan - 1);
            pastLoss1 = getLoss(progress.currentRestart, timeSpan - 1);

            avgLossPast.incrementBy(pastLoss0.times(minusOne));
            avgLossPast.incrementBy(pastLoss1);

            avgLossPresent.incrementBy(pastLoss1.times(minusOne));
            avgLossPresent.incrementBy(presentLoss);

            if (avgLossPast.greaterThan(avgLossPresent.plus(minDelta))){
                return true;
            } else {
                LOG.fine("Stopping this restart due to loss plateau: past loss " + avgLossPast + " vs. present: " + presentLoss);
                return false;
            }
        }
    }

    Value getLoss(Progress.Restart restart, int stepsBack) {
        switch (dataSelection) {
            case ONLINETRAIN:
                return restart.onlineTrainingResults.get(restart.onlineTrainingResults.size() - 1 - stepsBack).error;
            case TRUETRAIN:
                stepsBack /= recalculation;
                return restart.trueTrainingResults.get(restart.trueTrainingResults.size() - 1 - stepsBack).error;
            case VALIDATION:
                stepsBack /= recalculation;
                return restart.validationResults.get(restart.validationResults.size() - 1 - stepsBack).error;
        }
        return restart.onlineTrainingResults.get(restart.onlineTrainingResults.size() - 1 - stepsBack).error;
    }

    @Override
    public void nextRestart() {

    }
}
