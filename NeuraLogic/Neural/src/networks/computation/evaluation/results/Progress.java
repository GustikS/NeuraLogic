package networks.computation.evaluation.results;

import utils.exporting.Exportable;
import utils.exporting.Exporter;

import java.util.LinkedList;
import java.util.List;

/**
 * Class for storing the evolution of errors during training for latter analysis and plotting.
 * <p>
 * Created by gusta on 8.3.17.
 */
public class Progress implements Exportable<Progress> {

    transient Restart currentRestart;

    public List<Restart> restarts;

    public TrainVal bestResults;

    public Progress() {
        restarts = new LinkedList<>();
    }

    public void addOnlineResults(Results next) {
        if (!currentRestart.onlineTrainingResults.isEmpty()) {
            Results results = currentRestart.onlineTrainingResults.get(currentRestart.onlineTrainingResults.size() - 1);
            if (results != bestResults.training)
                results.evaluations = null; //delete the particular outputs from the past Results to save space (store only statistics)
        }
        currentRestart.onlineTrainingResults.add(next);
    }

    public void addTrueResults(Results training, Results validation) {
        if (!currentRestart.trueTrainingResults.isEmpty()) {
            Results results = currentRestart.trueTrainingResults.get(currentRestart.trueTrainingResults.size() - 1);
            if (results != bestResults.training)
                results.evaluations = null; //delete the particular outputs from the past Results to save space (store only statistics)
        }
        currentRestart.trueTrainingResults.add(training);
        if (!currentRestart.validationResults.isEmpty()) {
            Results results = currentRestart.validationResults.get(currentRestart.validationResults.size() - 1);
            if (results != bestResults.validation)
                results.evaluations = null; //delete the particular outputs from the past Results to save space (store only statistics)
        }
        currentRestart.validationResults.add(validation);
    }

    public void nextRestart() {
        currentRestart = new Restart();
        restarts.add(currentRestart);
    }

    public int getEpochCount() {
        return currentRestart.onlineTrainingResults.size();
    }

    public Results getCurrentOnlineTrainingResults() {
        return currentRestart.onlineTrainingResults.get(currentRestart.onlineTrainingResults.size() - 1);
    }

    public TrainVal getLastTrueResults() {
        return new TrainVal(currentRestart.trueTrainingResults.get(currentRestart.trueTrainingResults.size() - 1), currentRestart.validationResults.get(currentRestart.validationResults.size() - 1));
    }

    public class Restart {
        public List<Results> onlineTrainingResults = new LinkedList<>();
        public List<Results> trueTrainingResults = new LinkedList<>();
        public List<Results> validationResults = new LinkedList<>();
    }

    public static class TrainVal implements Exportable<TrainVal> {
        public Results training;
        public Results validation;

        public TrainVal(Results train, Results validation) {
            this.training = train;
            this.validation = validation;
        }

        /**
         * Decide what is better - whether to use training or validation or both
         *
         * @param other
         * @return
         */
        public boolean betterThan(TrainVal other) {
            if (other.validation.evaluations != null && !other.validation.evaluations.isEmpty()) {
                return validation.betterThan(other.validation);
            } else {
                return training.betterThan(other.training);
            }
        }
    }

    @Override
    public void export(Exporter exporter) {
        exporter.export(this);
    }
}