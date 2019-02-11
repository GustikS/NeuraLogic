package networks.computation.evaluation.results;

import java.util.LinkedList;
import java.util.List;

/**
 * Class for storing the evolution of errors during training for latter analysis and plotting.
 * <p>
 * Created by gusta on 8.3.17.
 */
public class Progress {

    Restart currentRestart;

    List<Restart> restarts;

    public TrainVal bestResults;

    public Progress() {
        currentRestart = new Restart();
        restarts = new LinkedList<>();
    }

    public void addOnlineResults(Results next) {
        currentRestart.onlineTrainingResults.get(currentRestart.onlineTrainingResults.size() - 1).evaluations = null; //delete the particular outputs from the past Results to save space (store only statistics)
        currentRestart.onlineTrainingResults.add(next);
    }

    public void addTrueResults(Results training, Results validation) {
        currentRestart.trueTrainingResults.get(currentRestart.trueTrainingResults.size() - 1).evaluations = null; //delete the particular outputs from the past Results to save space (store only statistics)
        currentRestart.trueTrainingResults.add(training);
        currentRestart.validationResults.get(currentRestart.validationResults.size() - 1).evaluations = null; //delete the particular outputs from the past Results to save space (store only statistics)
        currentRestart.validationResults.add(validation);
    }

    public void nextRestart() {
        restarts.add(currentRestart);
        currentRestart = new Restart();
    }

    public Results getCurrentOnlineTrainingResults() {
        return currentRestart.onlineTrainingResults.get(currentRestart.onlineTrainingResults.size() - 1);
    }

    public TrainVal getLastTrueResults() {
        return new TrainVal(currentRestart.trueTrainingResults.get(currentRestart.trueTrainingResults.size() - 1), currentRestart.validationResults.get(currentRestart.validationResults.size() - 1));
    }

    private class Restart {
        List<Results> onlineTrainingResults = new LinkedList<>();
        List<Results> trueTrainingResults = new LinkedList<>();
        List<Results> validationResults = new LinkedList<>();
    }

    public static class TrainVal {
        Results training;
        Results validation;

        public TrainVal(Results train, Results validation) {
            this.training = train;
            this.validation = validation;
        }

        /**
         * Decide what is better - whether to use training or validation or both
         * @param other
         * @return
         */
        public boolean betterThan(TrainVal other) {
            if (!other.validation.evaluations.isEmpty()) {
                return validation.betterThan(other.validation);
            } else {
                return training.betterThan(training);
            }
        }
    }
}