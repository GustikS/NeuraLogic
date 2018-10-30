package networks.computation.training.trainingStrategies;

import learning.crossvalidation.splitting.Splitter;
import learning.crossvalidation.splitting.StratifiedSplitter;
import networks.computation.evaluation.Evaluation;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Results;
import networks.computation.evaluation.values.Value;
import networks.computation.training.Backpropagation;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.trainingStrategies.Hyperparameters.LearnRateDecayStrategy;
import networks.computation.training.trainingStrategies.Hyperparameters.RestartingStrategy;
import settings.Settings;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public abstract class TrainingStrategy {
    private static final Logger LOG = Logger.getLogger(TrainingStrategy.class.getName());

    Settings settings;

    NeuralModel model;
    List<NeuralSample> sampleList;

    Splitter<NeuralSample> splitter = new StratifiedSplitter<>();

    Evaluation evaluation;
    Backpropagation backpropagation;

    Progress progress = new Progress();
    RestartingStrategy restartingStrategy;
    LearnRateDecayStrategy learnRateDecayStrategy;

    double learningRate = settings.learningRate;

    /**
     * Mapping from Weight.index -> Value (faster than hashmap)
     */
    Value[] bestWeights;

    public TrainingStrategy(Settings settings, NeuralModel model, List<NeuralSample> sampleList) {
        this.settings = settings;
        this.model = model;
        this.sampleList = sampleList;
        evaluation = new Evaluation(settings);
        backpropagation = new Backpropagation(settings);
    }

    public TrainingStrategy(Settings settings, NeuralModel model, List<NeuralSample> sampleList, Evaluation evaluation, Backpropagation backpropagation) {
        this.settings = settings;
        this.model = model;
        this.sampleList = sampleList;
        this.evaluation = evaluation;
        this.backpropagation = backpropagation;
    }

    public Results train() {
        initTraining();
        Collections.shuffle(sampleList, settings.random);
        for (int i = 0; i < settings.restartCount; i++) {
            initRestart();
            int epochae = 0;
            while (continueLearning() && epochae++ < settings.maxEpochCount) {
                initEpoch(epochae);
                learnEpoch();
            }
        }
        return finish();
    }

    protected boolean continueLearning() {
        return restartingStrategy.continueRestart();
    }

    protected abstract void initTraining();

    protected abstract void initRestart();

    protected void initEpoch(int count){
        if (settings.islearnRateDecay){
            learnRateDecayStrategy.decay();
        }
    }

    protected abstract void learnEpoch();

    protected abstract Results finish();

    public void setBestWeights() {
        for (int i = 0; i < bestWeights.length; i++) {
            model.weights.get(i).value = bestWeights[i];
        }
    }

    public static TrainingStrategy getFrom(Settings settings, NeuralModel model, List<NeuralSample> sampleList) {
        if (settings.minibatchSize > 1) {
            return new MiniBatchTraining(settings, model, sampleList);
        } else {
            return new SimpleTraining(settings, model, sampleList);
        }
    }
}
