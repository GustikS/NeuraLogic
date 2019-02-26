package networks.computation.training.strategies;

import learning.crossvalidation.splitting.Splitter;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.results.Results;
import networks.computation.iteration.actions.Evaluation;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.strategies.Hyperparameters.LearnRateDecayStrategy;
import networks.computation.training.strategies.Hyperparameters.RestartingStrategy;
import networks.computation.training.strategies.trainers.AsyncParallelTrainer;
import networks.computation.training.strategies.trainers.ListTrainer;
import networks.computation.training.strategies.trainers.MiniBatchTrainer;
import networks.computation.training.strategies.trainers.SequentialTrainer;
import settings.Settings;
import utils.generic.Pair;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 */
public class IterativeTrainingStrategy extends TrainingStrategy {
    private static final Logger LOG = Logger.getLogger(IterativeTrainingStrategy.class.getName());

    NeuralModel bestModel;

    List<NeuralSample> trainingSet;

    List<NeuralSample> validationSet;

    Progress progress;

    RestartingStrategy restartingStrategy;

    LearnRateDecayStrategy learnRateDecayStrategy;

    ListTrainer trainer;

    /**
     * Iterative strategy needs a separrate {@link Evaluation} on its own (not just in the trainer).
     */
    Evaluation evaluation;

    public IterativeTrainingStrategy(Settings settings, NeuralModel model, List<NeuralSample> sampleList) {
        super(settings, model);
        this.trainer = getTrainerFrom(settings);
        this.currentModel = model.cloneWeights();
        this.bestModel = this.currentModel;

        Pair<List<NeuralSample>, List<NeuralSample>> trainVal = trainingValidationSplit(sampleList);
        this.trainingSet = trainVal.r;
        this.validationSet = trainVal.s;

        this.learnRateDecayStrategy = LearnRateDecayStrategy.getFrom(settings, learningRate);
        this.evaluation = new Evaluation(settings);    //todo check do we need to parallelize here?
    }

    private Pair<List<NeuralSample>, List<NeuralSample>> trainingValidationSplit(List<NeuralSample> sampleList) {
        Splitter<NeuralSample> sampleSplitter = Splitter.getSplitter(settings);
        List<List<NeuralSample>> partition = sampleSplitter.partition(sampleList, 2);
        return new Pair<>(partition.get(0), partition.get(1));
    }

    private ListTrainer getTrainerFrom(Settings settings) {
        if (settings.asyncParallelTraining) {
            return new AsyncParallelTrainer(settings, currentModel).new AsyncListTrainer();
        } else if (settings.minibatchSize > 1) {
            return new MiniBatchTrainer(settings, currentModel, settings.minibatchSize).new MinibatchListTrainer();
        } else {
            return new SequentialTrainer(settings, currentModel).new SequentialListTrainer();
        }
    }

    public Pair<NeuralModel, Progress> train() {
        initTraining();
        int epochae = 0;
        for (int restart = 0; restart < settings.restartCount; restart++) {
            initRestart();
            while (restartingStrategy.continueRestart() && epochae++ < settings.maxCumEpochCount) {
                initEpoch(epochae);
                List<Result> onlineEvaluations = trainer.learnEpoch(currentModel, trainingSet);
                endEpoch(epochae, onlineEvaluations);
            }
            endRestart();
        }
        return finish();
    }


    protected void initTraining() {
        Collections.shuffle(trainingSet, settings.random);
        progress = new Progress();
        TrainVal evaluations = evaluateModel();
        progress.addTrueResults(resultsFactory.createFrom(evaluations.training), resultsFactory.createFrom(evaluations.validation));
    }

    protected void initRestart() {
        progress.nextRestart();
    }

    /**
     * What to do with the samples and learning process before each epoch iteration, i.e. load, shuffle, setup hyperparameters, etc.
     *
     * @param epochNumber
     */
    protected void initEpoch(int epochNumber) {
        if (settings.shuffleEachEpoch) {
            Collections.shuffle(trainingSet);
        }
        if (settings.islearnRateDecay) {
            learningRate = learnRateDecayStrategy.decay();
        }
        if (settings.dropoutMode == Settings.DropoutMode.LIFTED_DROPCONNECT && settings.dropoutRate > 0) {
            currentModel.dropoutWeights();
        }
    }

    protected void endEpoch(int count, List<Result> onlineEvaluations) {
        Results onlineResults = resultsFactory.createFrom(onlineEvaluations);
        progress.addOnlineResults(onlineResults);
        if (count % settings.resultsRecalculationEpochae == 0) {
            recalculateResults();
        }
    }

    protected void endRestart() {
        recalculateResults();
    }

    protected Pair<NeuralModel, Progress> finish() {
        evaluateModel(bestModel);
        return new Pair<>(bestModel, progress);
    }

    private TrainVal evaluateModel(NeuralModel neuralModel) {
        currentModel.loadWeights(neuralModel);
        return evaluateModel();
    }

    private TrainVal evaluateModel() {
        List<Result> trainingResults = trainingSet.parallelStream().map(s -> evaluation.evaluate(s)).collect(Collectors.toList());
        List<Result> validationResults = validationSet.parallelStream().map(s -> evaluation.evaluate(s)).collect(Collectors.toList());
        return new TrainVal(trainingResults, validationResults);
    }

    private void recalculateResults() {
        TrainVal trueEvaluations = evaluateModel();
        Results trainingResults = resultsFactory.createFrom(trueEvaluations.training);
        Results validationResults = resultsFactory.createFrom(trueEvaluations.validation);
        progress.addTrueResults(trainingResults, validationResults);

        Progress.TrainVal trainVal = new Progress.TrainVal(trainingResults, validationResults);
        saveIfBest(trainVal);
    }

    private void saveIfBest(Progress.TrainVal trainVal) {
        if (trainVal.betterThan(progress.bestResults)) {
            bestModel = currentModel.cloneWeights();
            progress.bestResults = trainVal;
        }
    }

    public NeuralModel getBestModel() {
        return bestModel;
    }
}