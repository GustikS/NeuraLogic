package networks.computation.training.strategies;

import ida.utils.tuples.Pair;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Results;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import settings.Settings;

import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TrainingStrategy {
    private static final Logger LOG = Logger.getLogger(TrainingStrategy.class.getName());

    Settings settings;

    NeuralModel currentModel;

    double learningRate;

    Results.Factory resultsFactory;

    public TrainingStrategy(Settings settings, NeuralModel model) {
        this.settings = settings;
        this.learningRate = settings.initLearningRate;
        this.currentModel = model;
        this.resultsFactory = Results.Factory.getFrom(settings);
    }

    public abstract Pair<NeuralModel, Progress> train();

    public static TrainingStrategy getFrom(Settings settings, NeuralModel model, Stream<NeuralSample> sampleStream) {
        if (settings.neuralStreaming) {
            return new StreamTrainingStrategy(settings, model, sampleStream);
        } else {
            return new IterativeTrainingStrategy(settings, model, sampleStream.collect(Collectors.toList()));
        }
    }
}