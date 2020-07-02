package cz.cvut.fel.ida.learning.crossvalidation;

import com.sun.istack.internal.Nullable;
import cz.cvut.fel.ida.learning.LearningSample;
import cz.cvut.fel.ida.learning.crossvalidation.splitting.Splitter;
import cz.cvut.fel.ida.learning.results.*;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    public Pair<MeanStdResults.TrainValTest, TrainTestResults> aggregateResults(List<TrainTestResults> foldRunStatsList) {

        MeanStdResults.TrainValTest trainValTest = aggregateResultsMeanStd(foldRunStatsList);

        List<Result> allTrain = new ArrayList<>();
        List<Result> allValidation = new ArrayList<>();
        List<Result> allTest = new ArrayList<>();

        List<Progress.Restart> allRestarts = new ArrayList<>();

        int correctClassCount = 0;
        int allCount = 0;

        for (TrainTestResults trainTestResults : foldRunStatsList) {    //todo now now test this crossval output
            Results training = trainTestResults.training.bestResults.training;

            allTrain.addAll(training.evaluations);
            allValidation.addAll(trainTestResults.training.bestResults.validation.evaluations);
            allTest.addAll(trainTestResults.testing.evaluations);

            allRestarts.addAll(trainTestResults.training.restarts);

            if (settings.calculateBestThreshold && training instanceof DetailedClassificationResults) {   // pass the best threshold from training to validation set
                Double bestAccuracy = ((DetailedClassificationResults) trainTestResults.testing).computeBestAccuracy(trainTestResults.testing.evaluations, ((DetailedClassificationResults) training).bestThreshold);
                correctClassCount += Math.round(bestAccuracy * trainTestResults.testing.evaluations.size());
                allCount += trainTestResults.testing.evaluations.size();
            }
        }

        Results.Factory factory = Results.Factory.getFrom(settings.testResultsType, settings);
        Results allTrainResults = factory.createFrom(allTrain);
        Results allValidationResults = factory.createFrom(allValidation);
        Results allTestResults = factory.createFrom(allTest);

        if (settings.calculateBestThreshold && allTestResults instanceof ClassificationResults) {
            double mergedBestAccuracy = (double) correctClassCount / allCount;
            ((DetailedClassificationResults) allTestResults).bestAccuracy = mergedBestAccuracy;
        }

        Progress allTrainingMerged = new Progress();
        allTrainingMerged.restarts = allRestarts;
        allTrainingMerged.bestResults = new Progress.TrainVal(allTrainResults, allValidationResults);

        TrainTestResults finalTrainTestResults = new TrainTestResults(allTrainingMerged, allTestResults);
        Pair<MeanStdResults.TrainValTest, TrainTestResults> resultsPair = new Pair<>(trainValTest, finalTrainTestResults);
        return resultsPair;
    }

    public MeanStdResults.TrainValTest aggregateResultsMeanStd(List<TrainTestResults> foldRunStatsList) {

        List<Results> training = foldRunStatsList.stream().map(fold -> fold.training.bestResults.training).collect(Collectors.toList());
        List<Results> validation = foldRunStatsList.stream().map(fold -> fold.training.bestResults.validation).collect(Collectors.toList());
        List<Results> testing = foldRunStatsList.stream().map(fold -> fold.testing).collect(Collectors.toList());

        if (training.get(0) instanceof ClassificationResults) {
            MeanStdResults train = ClassificationResults.aggregateClassifications(training.stream().map(res -> (ClassificationResults) res).collect(Collectors.toList()));
            MeanStdResults val = ClassificationResults.aggregateClassifications(validation.stream().map(res -> (ClassificationResults) res).collect(Collectors.toList()));
            MeanStdResults test = ClassificationResults.aggregateClassifications(testing.stream().map(res -> (ClassificationResults) res).collect(Collectors.toList()));
            return new MeanStdResults.TrainValTest(train, val, test);
        } else {
            MeanStdResults train = RegressionResults.aggregateRegressions(training.stream().map(res -> (RegressionResults) res).collect(Collectors.toList()));
            MeanStdResults val = RegressionResults.aggregateRegressions(validation.stream().map(res -> (RegressionResults) res).collect(Collectors.toList()));
            MeanStdResults test = RegressionResults.aggregateRegressions(testing.stream().map(res -> (RegressionResults) res).collect(Collectors.toList()));
            return new MeanStdResults.TrainValTest(train, val, test);
        }
    }

    private List<T> extractSamples(List<Fold<T>> folds) {
        List<T> samples = new ArrayList<>();
        folds.forEach(fold -> samples.addAll(fold.test));
        return samples;
    }
}