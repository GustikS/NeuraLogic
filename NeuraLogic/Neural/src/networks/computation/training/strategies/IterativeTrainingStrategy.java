package networks.computation.training.strategies;

import learning.crossvalidation.splitting.Splitter;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.results.Results;
import networks.computation.evaluation.values.distributions.ValueInitializer;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.optimizers.Optimizer;
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

    ValueInitializer valueInitializer;

    public IterativeTrainingStrategy(Settings settings, NeuralModel model, List<NeuralSample> sampleList) {
        super(settings, model);
        this.trainer = getTrainerFrom(settings);
        this.currentModel = model.cloneWeights();
        this.bestModel = this.currentModel;
        this.valueInitializer = ValueInitializer.getInitializer(settings);

        Pair<List<NeuralSample>, List<NeuralSample>> trainVal = trainingValidationSplit(sampleList);
        this.trainingSet = trainVal.r;
        this.validationSet = trainVal.s;

        this.learnRateDecayStrategy = LearnRateDecayStrategy.getFrom(settings, learningRate);
        this.restartingStrategy = RestartingStrategy.getFrom(settings);
    }

    private Pair<List<NeuralSample>, List<NeuralSample>> trainingValidationSplit(List<NeuralSample> sampleList) {
        Splitter<NeuralSample> sampleSplitter = Splitter.getSplitter(settings);
        Pair<List<NeuralSample>, List<NeuralSample>> partition = sampleSplitter.partition(sampleList, settings.trainValidationPercentage);
        return new Pair<>(partition.r, partition.s);
    }

    private ListTrainer getTrainerFrom(Settings settings) {
        if (settings.asyncParallelTraining) {
            return new AsyncParallelTrainer(settings, Optimizer.getFrom(settings), currentModel).new AsyncListTrainer();
        } else if (settings.minibatchSize > 1) {
            return new MiniBatchTrainer(settings, Optimizer.getFrom(settings), currentModel, settings.minibatchSize).new MinibatchListTrainer();
        } else {
            return new SequentialTrainer(settings, Optimizer.getFrom(settings), currentModel).new SequentialListTrainer();
        }
    }

    public Pair<NeuralModel, Progress> train() {
        LOG.finer("Starting with iterative mode neural training.");
        initTraining();
        int epochae = 0;
        for (int restart = 0; restart < settings.restartCount; restart++) {
            initRestart();
            while (restartingStrategy.continueRestart(progress) && epochae++ < settings.maxCumEpochCount) {
                initEpoch(epochae);
                List<Result> onlineEvaluations = trainer.learnEpoch(currentModel, trainingSet);
                endEpoch(epochae, onlineEvaluations);
            }
            endRestart();
        }
        return finish();
    }

    protected void initTraining() {
        LOG.info("Initializing training (shuffling examples etc.)");
        if (settings.shuffleBeforeTraining) {
            Collections.shuffle(trainingSet, settings.random);
        }
        progress = new Progress();
    }

    protected void initRestart() {
        LOG.info("Initializing new restart (resetting weights).");
        currentModel.resetWeights(valueInitializer);
        progress.nextRestart();
        recalculateResults();
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
        LOG.info("epoch " + count + " online results : " + onlineResults);
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
        List<Result> trainingResults = trainer.evaluate(trainingSet);
        List<Result> validationResults = trainer.evaluate(validationSet);
        return new TrainVal(trainingResults, validationResults);
    }

    private void recalculateResults() {
        TrainVal trueEvaluations = evaluateModel();
        Results trainingResults = resultsFactory.createFrom(trueEvaluations.training);
        Results validationResults = resultsFactory.createFrom(trueEvaluations.validation);
        progress.addTrueResults(trainingResults, validationResults);
        LOG.info("true results :- train: " + trainingResults + ", validation: " + validationResults);

        Progress.TrainVal trainVal = new Progress.TrainVal(trainingResults, validationResults);
        saveIfBest(trainVal);
    }

    private void saveIfBest(Progress.TrainVal trainVal) {
        if (progress.bestResults == null || trainVal.betterThan(progress.bestResults)) {
            bestModel = currentModel.cloneWeights();
            progress.bestResults = trainVal;
        }
    }

    public NeuralModel getBestModel() {
        return bestModel;
    }
}