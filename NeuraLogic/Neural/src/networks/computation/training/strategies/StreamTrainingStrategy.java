package networks.computation.training.strategies;

import ida.utils.tuples.Pair;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.results.Results;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.strategies.trainers.MiniBatchTrainer;
import networks.computation.training.strategies.trainers.SequentialTrainer;
import networks.computation.training.strategies.trainers.StreamTrainer;
import settings.Settings;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//todo next introduce validation set!
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
            return new MiniBatchTrainer(settings, currentModel, settings.minibatchSize).new MinibatchStreamTrainer();
        } else {
            return new SequentialTrainer(settings, currentModel).new SequentialStreamTrainer();
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

    public NeuralModel getBestModel() {
        return currentModel;
    }
}