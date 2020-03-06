package networks.computation.training.strategies;

import learning.results.Progress;
import learning.results.Result;
import learning.results.Results;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.optimizers.Optimizer;
import networks.computation.training.strategies.debugging.NeuralDebugging;
import networks.computation.training.strategies.trainers.MiniBatchTrainer;
import networks.computation.training.strategies.trainers.SequentialTrainer;
import networks.computation.training.strategies.trainers.StreamTrainer;
import settings.Settings;
import utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTrainingStrategy extends TrainingStrategy {
    private static final Logger LOG = Logger.getLogger(StreamTrainingStrategy.class.getName());

    Stream<NeuralSample> samplesStream;

    StreamTrainer trainer;

    public StreamTrainingStrategy(Settings settings, NeuralModel model, Stream<NeuralSample> sampleStream) {
        super(settings, model);
        this.samplesStream = sampleStream;
        this.trainer = getTrainerFrom(settings);
    }

    private StreamTrainer getTrainerFrom(Settings settings) {
        if (settings.minibatchSize > 1) {
            return new MiniBatchTrainer(settings, Optimizer.getFrom(settings, learningRate), currentModel, settings.minibatchSize).new MinibatchStreamTrainer();
        } else {
            return new SequentialTrainer(settings, Optimizer.getFrom(settings, learningRate),currentModel).new SequentialStreamTrainer();
        }
    }

    public Pair<NeuralModel, Progress> train() {
        Stream<Result> resultStream = trainer.learnEpoch(currentModel, samplesStream);
        List<Result> resultList = resultStream.collect(Collectors.toList());
        Progress progress = new Progress();
        Results results = resultsFactory.createFrom(resultList);
        progress.addOnlineResults(results);
        return new Pair<>(currentModel, progress);
    }

    @Override
    public void setupDebugger(NeuralDebugging neuralDebugger) {
        trainer.setupDebugger(neuralDebugger);
    }

    public NeuralModel getBestModel() {
        return currentModel;
    }
}