package learning.crossvalidation;

import com.sun.istack.internal.Nullable;
import learning.LearningSample;
import learning.crossvalidation.splitting.Splitter;
import networks.computation.evaluation.results.ClassificationResults;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.results.Results;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by gusta on 8.3.17.
 * <p>
 * Crossval takes settings (and/or foldCount) to instantiate splitter.
 * It can then split incomming samples into folds.
 */
public class Crossvalidation<T extends LearningSample> {
    Settings settings;

    public int foldCount;
    Splitter<T> splitter;

    public List<Fold<T>> folds;

    List<T> samples;

    @Nullable
    Results results;

    public Crossvalidation(Settings settings) {
        this.settings = settings;
        foldCount = settings.foldsCount;
        splitter = Splitter.<T>getSplitter(settings);
    }

    public Crossvalidation(Settings settings, int foldCount) {
        folds = new ArrayList<>(foldCount);
        for (int i = 0; i < foldCount; i++) {
            folds.add(new Fold<>(i));
        }
        this.foldCount = foldCount;
        splitter = Splitter.<T>getSplitter(settings);
    }

    public void loadFolds(Stream<T> samples) {
        this.folds = splitter.splitIntoFolds(samples, foldCount);
        this.samples = extractSamples(this.folds);
    }

    public void loadFolds(List<Stream<T>> folds) {
        this.folds = splitter.splitIntoFolds(folds);
        this.samples = extractSamples(this.folds);
    }

    public TrainTestResults aggregateResults(List<TrainTestResults> foldRunStatsList) {
        List<Result> allTrain = new ArrayList<>();
        List<Result> allValidation = new ArrayList<>();
        List<Result> allTest = new ArrayList<>();

        List<Progress.Restart> allRestarts = new ArrayList<>();

        int correctClassCount = 0;
        int allCount = 0;

        for (TrainTestResults trainTestResults : foldRunStatsList) {    //todo now get also standard deviations
            Results training = trainTestResults.training.bestResults.training;

            allTrain.addAll(training.evaluations);
            allValidation.addAll(trainTestResults.training.bestResults.validation.evaluations);
            allTest.addAll(trainTestResults.testing.evaluations);

            allRestarts.addAll(trainTestResults.training.restarts);

            if (settings.calculateBestThreshold && training instanceof ClassificationResults) {   // pass the best threshold from training to validation set
                Double bestAccuracy = ((ClassificationResults) trainTestResults.testing).getBestAccuracy(trainTestResults.testing.evaluations, ((ClassificationResults) training).bestThreshold);
                correctClassCount += Math.round(bestAccuracy * trainTestResults.testing.evaluations.size());
                allCount += trainTestResults.testing.evaluations.size();
            }
        }

        Results.Factory factory = Results.Factory.getFrom(settings);
        Results allTrainResults = factory.createFrom(allTrain);
        Results allValidationResults = factory.createFrom(allValidation);
        Results allTestResults = factory.createFrom(allTest);

        if (settings.calculateBestThreshold && allTestResults instanceof ClassificationResults) {
            double mergedBestAccuracy = (double) correctClassCount / allCount;
            ((ClassificationResults) allTestResults).bestAccuracy = mergedBestAccuracy;
        }

        Progress allTrainingMerged = new Progress();
        allTrainingMerged.restarts = allRestarts;
        allTrainingMerged.bestResults = new Progress.TrainVal(allTrainResults, allValidationResults);

        TrainTestResults finalTrainTestResults = new TrainTestResults(allTrainingMerged, allTestResults);
        return finalTrainTestResults;
    }

    private List<T> extractSamples(List<Fold<T>> folds) {
        List<T> samples = new ArrayList<>();
        folds.forEach(fold -> samples.addAll(fold.test));
        return samples;
    }
}