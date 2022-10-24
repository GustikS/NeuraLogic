package cz.cvut.fel.ida.neural.networks.computation.training.strategies;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.learning.LearningSample;
import cz.cvut.fel.ida.learning.crossvalidation.splitting.Splitter;
import cz.cvut.fel.ida.learning.results.DetailedClassificationResults;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.learning.results.Results;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Accumulating;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.SaturationChecker;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.optimizers.Optimizer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters.LearnRateDecayStrategy;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters.RestartingStrategy;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging.NeuralDebugging;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.AsyncParallelTrainer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.ListTrainer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.MiniBatchTrainer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.SequentialTrainer;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.exporting.TextExporter;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Classic iterative optimization/learning
 */
public class IterativeTrainingStrategy extends TrainingStrategy {
    private static final Logger LOG = Logger.getLogger(IterativeTrainingStrategy.class.getName());

    transient NeuralModel bestModel;

    transient List<NeuralSample> trainingSet;

    transient List<NeuralSample> validationSet;

    Progress progress;

    RestartingStrategy restartingStrategy;

    LearnRateDecayStrategy learnRateDecayStrategy;

    transient ListTrainer trainer;

    ValueInitializer valueInitializer;

    private final int resultsRecalculationEpochae;

    public IterativeTrainingStrategy(Settings settings, NeuralModel model, List<NeuralSample> sampleList) {
        super(settings, model);
        this.trainer = getTrainerFrom(settings);
        this.bestModel = this.currentModel;
        this.valueInitializer = ValueInitializer.getInitializer(settings);

        Pair<List<NeuralSample>, List<NeuralSample>> trainVal = trainingValidationSplit(sampleList);
        this.trainingSet = trainVal.r;
        this.validationSet = trainVal.s;

        this.learnRateDecayStrategy = LearnRateDecayStrategy.getFrom(settings, learningRate);   //passes the single reference to learningRate shared by others
        this.restartingStrategy = RestartingStrategy.getFrom(settings, !validationSet.isEmpty());

        this.resultsRecalculationEpochae = settings.resultsRecalculationEpochae;
//        this.trainingDebugger = new TrainingDebugger(settings);
    }

    private Pair<List<NeuralSample>, List<NeuralSample>> trainingValidationSplit(List<NeuralSample> sampleList) {
        List<NeuralSample> extraValidation = sampleList.stream().filter(s -> s.type == LearningSample.Split.VALIDATION).collect(Collectors.toList());
        if (!extraValidation.isEmpty()) {
            LOG.fine("Splitting back the train-validation dataset according to the given input splits");
            List<NeuralSample> trainOnly = new ArrayList<>(sampleList);
            trainOnly.removeAll(extraValidation);
            LOG.fine("Train-set size=" + trainOnly.size() + ", Validation-set size=" + extraValidation.size());
            return new Pair<>(trainOnly, extraValidation);
        }
        LOG.info("Preparing the train-validation dataset split with percentage: " + settings.trainValidationPercentage);
        Splitter<NeuralSample> sampleSplitter = Splitter.getSplitter(settings);
        Pair<List<NeuralSample>, List<NeuralSample>> partition = sampleSplitter.partition(sampleList, settings.trainValidationPercentage);
        LOG.info("Train-set size=" + partition.r.size() + ", Validation-set size=" + partition.s.size());
        return new Pair<>(partition.r, partition.s);
    }

    private ListTrainer getTrainerFrom(Settings settings) {
        if (settings.asyncParallelTraining) {
            return new AsyncParallelTrainer(settings, Optimizer.getFrom(settings, learningRate), currentModel).new AsyncListTrainer();
        } else if (settings.minibatchSize > 1) {
            return new MiniBatchTrainer(settings, Optimizer.getFrom(settings, learningRate), currentModel, settings.minibatchSize).new MinibatchListTrainer();
        } else {
            return new SequentialTrainer(settings, Optimizer.getFrom(settings, learningRate), currentModel).new SequentialListTrainer();
        }
    }

    public Pair<NeuralModel, Progress> train() {
        timing.tic();

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
            if (epochae > settings.maxCumEpochCount){
                break;
            }
        }

        timing.toc();
        timing.finish();
        return finish();
    }

    @Override
    public void setupDebugger(NeuralDebugging neuralDebugger) {
        this.trainer.setupDebugger(neuralDebugger);
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
        restart += 1;
        setupExporter();

        trainer.restart(settings);
        currentModel.resetWeights(valueInitializer);
        progress.nextRestart();
        recalculateResults();   //todo investigate initial jump up in error - is there any still? may difference between online vs. true calculation
        LOG.finer("New restart has been initialized");
    }

    /**
     * What to do with the samples and learning process before each epoch iteration, i.e. load, shuffle, setup hyperparameters, etc.
     *
     * @param epochNumber
     */
    protected void initEpoch(int epochNumber) {
        if (settings.shuffleEachEpoch) {
            Collections.shuffle(trainingSet, settings.random);
        }
        if (settings.islearnRateDecay) {
            learnRateDecayStrategy.decay(epochNumber);
        }
        if (settings.dropoutMode == Settings.DropoutMode.LIFTED_DROPCONNECT && settings.dropoutRate > 0) {
            currentModel.dropoutWeights();
        }
    }

    protected void endEpoch(int count, List<Result> onlineEvaluations) {
        if (count % settings.resultsRecalculationEpochae == 0) {
            recalculateResults();
            if (settings.debugTemplateTraining && trainingDebugCallback != null) {
                Map<Integer, Weight> integerWeightMap = currentModel.mapWeightsToIds();
                trainingDebugCallback.accept(integerWeightMap);
            }
        } else {
            Results onlineResults = trainOnlineResultsFactory.createFrom(onlineEvaluations);
            progress.addOnlineResults(onlineResults);
            exportProgress(onlineResults);
            LOG.info("epoch: " + count + " : online results : " + onlineResults.toString(settings));
        }
    }

    protected void endRestart() {
        LOG.info("Finished restart, recalculating last true results.");
        recalculateResults();
        exporter.delimitEnd();
        exporter.finish();
        restartingStrategy.nextRestart();
    }

    protected Pair<NeuralModel, Progress> finish() {
        LOG.info("Finished training, loading best model so far.");
        evaluateModel(bestModel);
        logSampleOutputs();

        super.endTrainingStrategy();    //e.g. restore the world state
        return new Pair<>(bestModel, progress);
    }

    private void evaluateModel(NeuralModel neuralModel) {
        currentModel.loadWeightValues(neuralModel);
        recalculateResults();
    }

    private TrainVal evaluateModel() {
        List<Result> trainingResults = trainer.evaluate(trainingSet);
        List<Result> validationResults = trainer.evaluate(validationSet);
        return new TrainVal(trainingResults, validationResults);
    }

    private void recalculateResults() {
        TrainVal trueEvaluations = evaluateModel();
        Results trainingResults = trainRecalculationResultsFactory.createFrom(trueEvaluations.training);
        Results validationResults = validationResultsFactory.createFrom(trueEvaluations.validation);
        if (settings.calculateBestThreshold && validationResults instanceof DetailedClassificationResults) {   // pass the best threshold from training to validation set
            Value threshold = ((DetailedClassificationResults) trainingResults).computeBestAccuracyThreshold(trainingResults.evaluations);
            ((DetailedClassificationResults) validationResults).computeBestAccuracy(validationResults.evaluations, threshold);
        }
        if (settings.passResultsCache) {
            validationResultsFactory.cacheForReuse(trainingResults);
        }

        progress.addTrueResults(trainingResults, validationResults);

        if (LOG.isLoggable(Level.FINE)) {
            String msg = "true results :- train: " + trainingResults.toString(settings);
            if (!validationResults.isEmpty()) {
                msg += ",\n val: " + validationResults.toString(settings);
            }
            LOG.fine(msg);
        }

        if (settings.debugSampleOutputs) {
            logSampleOutputs();
        }

        if (settings.checkNeuronSaturation) {
            saturationCheck(trainingSet);
            saturationCheck(validationSet);
        }

        Progress.TrainVal trainVal = new Progress.TrainVal(trainingResults, validationResults);
        exportProgress(trainVal);
        saveIfBest(trainVal);
    }

    private void saveIfBest(Progress.TrainVal trainVal) {
        if (progress.bestResults == null || trainVal.betterThan(progress.bestResults, settings.dataSelection, settings.modelSelection)) {
            LOG.fine("Improvement of best " + (settings.dataSelection != Settings.DataSelection.VALIDATION || validationSet.isEmpty() ? "training " : "validation ") + settings.modelSelection.name() + " stored so far...");
            bestModel = currentModel.cloneWeights();
            if (settings.calculateBestThreshold && trainVal.training instanceof DetailedClassificationResults) {
                bestModel.threshold = ((DetailedClassificationResults) trainVal.training).bestThreshold;
            }
            progress.bestResults = trainVal;
        }
    }

    private void logSampleOutputs() {
        LOG.finer("Training outputs");
        LOG.finer(progress.getLastTrueResults().training.printOutputs(false).toString());
        TextExporter.exportString(progress.getLastTrueResults().training.printOutputs(true).toString(), Paths.get(settings.exportDir, "outputs/train" + counter + ".txt"));

        if (!progress.getLastTrueResults().validation.isEmpty()) {
            LOG.finer("Validation outputs:");
            LOG.finer(progress.getLastTrueResults().validation.printOutputs(false).toString());
            TextExporter.exportString(progress.getLastTrueResults().validation.printOutputs(true).toString(), Paths.get(settings.exportDir, "outputs/val" + counter + ".txt"));
        }
    }

    private void saturationCheck(List<NeuralSample> samples) {
        ScalarValue percentage = new ScalarValue(settings.saturationPercentage);
        Accumulating accumulating = new Accumulating(settings, new SaturationChecker());
        List<Pair<Value, Value>> pairs = accumulating.accumulateStats(samples);
        int saturatedNetworks = 0;
        for (Pair<Value, Value> pair : pairs) {
            Value saturated = pair.r;
            Value all = pair.s;
            if (saturated.greaterThan(all.times(percentage))) {
                saturatedNetworks++;
            }
        }
        if (saturatedNetworks > 0) {
            LOG.warning("There are saturated networks: #" + saturatedNetworks + " / " + samples.size());
        }
    }

    private void exportProgress(Exportable results) {
        results.export(exporter);
        exporter.delimitNext();
    }
}