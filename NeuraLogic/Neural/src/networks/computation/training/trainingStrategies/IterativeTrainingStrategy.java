package networks.computation.training.trainingStrategies;

import ida.utils.tuples.Pair;
import networks.computation.evaluation.Evaluation;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.results.Results;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.trainingStrategies.Hyperparameters.LearnRateDecayStrategy;
import networks.computation.training.trainingStrategies.Hyperparameters.RestartingStrategy;
import settings.Settings;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * todo add a streaming version
 */
public class IterativeTrainingStrategy extends TrainingStrategy {
    private static final Logger LOG = Logger.getLogger(IterativeTrainingStrategy.class.getName());

    NeuralModel bestModel;

    List<NeuralSample> sampleList;

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
        this.trainer = getTrainer(settings);
        this.currentModel = model.cloneWeights();
        this.bestModel = this.currentModel;
        this.sampleList = sampleList;
        this.learnRateDecayStrategy = LearnRateDecayStrategy.getFrom(settings, learningRate);
        this.evaluation =  new Evaluation(settings);    //todo check do we need to parallelize here?
    }

    private ListTrainer getTrainer(Settings settings) {
        if (settings.minibatchSize > 1) {
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
                List<Result> onlineEvaluations = trainer.learnEpoch(currentModel, sampleList);
                endEpoch(epochae, onlineEvaluations);
            }
            endRestart();
        }
        return finish();
    }


    protected void initTraining() {
        Collections.shuffle(sampleList, settings.random);
        progress = new Progress();
        List<Result> evaluations = evaluateModel();
        progress.addTrueResults(resultsFactory.createFrom(evaluations));
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
            Collections.shuffle(sampleList);
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
            saveIfBest();
        }
    }

    protected void endRestart() {
        saveIfBest();
    }

    protected Pair<NeuralModel, Progress> finish() {
        evaluateModel(bestModel);
        return new Pair<>(bestModel, progress);
    }

    private List<Result> evaluateModel(NeuralModel neuralModel) {
        currentModel.loadWeights(neuralModel);
        return evaluateModel();
    }

    private List<Result> evaluateModel() {
        return sampleList.parallelStream().map(s -> evaluation.evaluate(s)).collect(Collectors.toList());
    }

    private void saveIfBest() {
        List<Result> trueEvaluations = evaluateModel();
        Results currentResults = resultsFactory.createFrom(trueEvaluations);
        progress.addTrueResults(currentResults);
        if (currentResults.betterThan(progress.bestResults)) {
            bestModel = currentModel.cloneWeights();
            progress.bestResults = currentResults;
        }
    }

    public NeuralModel getBestModel() {
        return bestModel;
    }
}