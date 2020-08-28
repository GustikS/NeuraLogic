package cz.cvut.fel.ida.neural.networks.computation.training.strategies;

import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.learning.results.Results;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.optimizers.Optimizer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging.NeuralDebugging;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.MiniBatchTrainer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.SequentialTrainer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.StreamTrainer;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A single pass through the dataset only
 */
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
            return new SequentialTrainer(settings, Optimizer.getFrom(settings, learningRate), currentModel).new SequentialStreamTrainer();
        }
    }

    public Pair<NeuralModel, Progress> train() {
        Stream<Result> resultStream = trainer.learnEpoch(currentModel, samplesStream);
        List<Result> resultList = resultStream.collect(Collectors.toList());
        resultStream.close();
        Progress progress = new Progress();
        Results results = trainRecalculationResultsFactory.createFrom(resultList);
        progress.nextRestart();
        progress.addOnlineResults(results);
        progress.bestResults = new Progress.TrainVal(results, null);
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