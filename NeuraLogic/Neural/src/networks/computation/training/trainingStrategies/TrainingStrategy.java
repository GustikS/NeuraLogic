package networks.computation.training.trainingStrategies;

import networks.computation.evaluation.Evaluation;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.results.Results;
import networks.computation.iteration.NeuronIterating;
import networks.computation.iteration.actions.Dropouter;
import networks.computation.training.Backpropagation;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.trainingStrategies.Hyperparameters.LearnRateDecayStrategy;
import networks.computation.training.trainingStrategies.Hyperparameters.RestartingStrategy;
import org.jetbrains.annotations.NotNull;
import settings.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public abstract class TrainingStrategy {
    private static final Logger LOG = Logger.getLogger(TrainingStrategy.class.getName());

    Settings settings;

    NeuralModel currentModel;
    NeuralModel bestModel;

    List<NeuralSample> sampleList;

    Evaluation evaluation;
    Backpropagation backpropagation;

    Progress progress;
    Results.Factory resultsFactory;

    RestartingStrategy restartingStrategy;

    LearnRateDecayStrategy learnRateDecayStrategy;
    double learningRate;

    Dropouter dropouter;

    public TrainingStrategy(Settings settings, NeuralModel model, List<NeuralSample> sampleList) {
        this.settings = settings;
        this.currentModel = model.cloneWeights();
        this.bestModel = this.currentModel;
        this.sampleList = sampleList;
        evaluation = new Evaluation(settings);
        backpropagation = new Backpropagation(settings);
        resultsFactory = Results.Factory.getFrom(settings);
        learningRate = settings.learningRate;
        learnRateDecayStrategy = LearnRateDecayStrategy.getFrom(settings, learningRate);
    }

    public TrainingStrategy(Settings settings, NeuralModel model, List<NeuralSample> sampleList, Evaluation evaluation, Backpropagation backpropagation) {
        this.settings = settings;
        this.currentModel = model.cloneWeights();
        this.bestModel = this.currentModel;
        this.sampleList = sampleList;
        this.evaluation = evaluation;
        this.backpropagation = backpropagation;
    }

    //todo next return Progress instead of just Results ? or better yet Pair<NeuralModel, Progress> - redo in pipelines
    public Results train() {
        initTraining();
        int epochae = 0;
        for (int restart = 0; restart < settings.restartCount; restart++) {
            initRestart();
            while (restartingStrategy.continueRestart() && epochae++ < settings.maxCumEpochCount) {
                initEpoch(epochae);
                List<Result> onlineEvaluations = learnEpoch(epochae);
                endEpoch(epochae, onlineEvaluations);
            }
            endRestart();
        }
        return finish();
    }

    protected void initTraining() {
        Collections.shuffle(sampleList, settings.random);
        progress = new Progress();
        List<Result> evaluations = evaluateSamples();
        progress.addTrueResults(resultsFactory.createFrom(evaluations));
    }

    protected void initRestart() {
        progress.nextRestart();
    }

    protected void initEpoch(int epochNumber) {
        if (settings.islearnRateDecay) {
            learningRate = learnRateDecayStrategy.decay();
        }
        if (settings.dropoutRate > 0) {
            dropoutNetworks();
        }
    }

    protected abstract List<Result> learnEpoch(int epochNumber);

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

    protected Results finish() {
        evaluateSamples(bestModel);
        return progress.getLastTrueResults();
    }

    @NotNull
    private List<Result> evaluateSamples() {
        List<Result> results = new ArrayList<>(sampleList.size());
        for (NeuralSample sample : sampleList) {
            Result evaluation = this.evaluation.evaluate(sample);
            results.add(evaluation);
        }
        return results;
    }

    private List<Result> evaluateSamples(NeuralModel neuralModel) {
        currentModel.loadWeights(neuralModel);
        return evaluateSamples();
    }

    private void dropoutNetworks() {
        if (settings.dropoutMode == Settings.DropoutMode.LIFTED_DROPCONNECT) {
            currentModel.dropoutWeights();
        } else if (settings.dropoutMode == Settings.DropoutMode.DROPOUT) {
            for (NeuralSample neuralSample : sampleList) {
                NeuronIterating dropouter = Dropouter.getFor(settings, neuralSample.query);
                dropouter.iterate();
            }

        }
    }

    private void saveIfBest() {
        List<Result> trueEvaluations = evaluateSamples();
        Results currentResults = resultsFactory.createFrom(trueEvaluations);
        progress.addTrueResults(currentResults);
        if (currentResults.betterThan(progress.bestResults)) {
            bestModel = currentModel.cloneWeights();
            progress.bestResults = currentResults;
        }
    }

    public static TrainingStrategy getFrom(Settings settings, NeuralModel model, List<NeuralSample> sampleList) {
        if (settings.minibatchSize > 1) {
            return new MiniBatchTraining(settings, model, sampleList);
        } else {
            return new SimpleTraining(settings, model, sampleList);
        }
    }

    public NeuralModel getBestModel() {
        return bestModel;
    }
}
