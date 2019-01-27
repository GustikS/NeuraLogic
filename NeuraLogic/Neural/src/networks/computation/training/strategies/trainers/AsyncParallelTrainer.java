package networks.computation.training.strategies.trainers;

import networks.computation.evaluation.results.Result;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import settings.Settings;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The experimental unsynchronized parallel minibatch/SGD with random gradient overwriting.
 * <p>
 * This is roughly like minibatch training where the batch size is app. the number of available threads/cores,
 * but it can vary dynamically as the samples to be processed in parallel are selected by the underlying {@link java.util.Spliterator} of the parallel stream.
 * <p>
 * The correct way of processing is to perform weight updates BEFORE any subsequent evaluation and next weight updates,
 * but in this case we do not wait until the weight update is finished, so the updates may overwrite each other.
 * But since there is no synchronization it should be fastest and it was shown to work in practice on our datasets.
 */
public class AsyncParallelTrainer extends SequentialTrainer {
    private static final Logger LOG = Logger.getLogger(AsyncParallelTrainer.class.getName());

    //todo correctly there should be as many trainers as there are samples, i.e. no two samples should be trained with the same trainer in parallel
    // otherwise they might be overwriting/mixing up their results in shared neurons, but this trainer is kind of flawed anyway

    public AsyncParallelTrainer(Settings settings, NeuralModel neuralModel) {
        super(settings, neuralModel);
    }

    protected AsyncParallelTrainer() {
    }

    public class AsyncListTrainer extends AsyncParallelTrainer implements ListTrainer {


        @Override
        public List<Result> learnEpoch(NeuralModel neuralModel, List<NeuralSample> sampleList) {
            List<Result> resultList = sampleList.parallelStream().
                    map(neuralSample -> learnFromSample(neuralModel, neuralSample, dropout, invalidation, evaluation, backpropagation)).collect(Collectors.toList());
            return resultList;
        }
    }

    public class AsyncStreamTrainer extends AsyncParallelTrainer implements StreamTrainer {

        @Override
        public Stream<Result> learnEpoch(NeuralModel neuralModel, Stream<NeuralSample> sampleStream) {
            Stream<Result> resultStream = sampleStream.parallel().map(sample -> learnFromSample(neuralModel, sample, dropout, invalidation, evaluation, backpropagation));
            return resultStream;
        }
    }
}

