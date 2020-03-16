package cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers;

import cz.cvut.fel.ida.utils.generic.Utilities;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.optimizers.Optimizer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging.NeuralDebugging;
import cz.cvut.fel.ida.setup.Settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Classical neural training using minibatches, uncluding the standard iterative but also the streaming version, i.e.
 * where the input to learn from can be a stream of samples!
 */
public class MiniBatchTrainer extends Trainer {
    private static final Logger LOG = Logger.getLogger(MiniBatchTrainer.class.getName());

    int minibatchSize;

    /**
     * Each sample in a minibatch will have its own {@link SequentialTrainer}
     */
    List<SequentialTrainer> trainers;

    private MiniBatchTrainer() {
    }

    public MiniBatchTrainer(Settings settings, Optimizer optimizer, NeuralModel neuralModel, int minibatchSize) {
        super(settings, optimizer);
        this.minibatchSize = minibatchSize;
        trainers = new ArrayList<>(minibatchSize);
        for (int i = 0; i < minibatchSize; i++) {
            trainers.add(new SequentialTrainer(settings, optimizer, neuralModel, i));
        }
    }

    /**
     * Learning by iterating a given {@link List} of samples in minibatches.
     */
    public class MinibatchListTrainer implements ListTrainer {

        @Override
        public List<Result> learnEpoch(NeuralModel neuralModel, List<NeuralSample> sampleList) {
            List<Result> resultList = new ArrayList<>(sampleList.size());
            MiniBatchIterator miniBatchIterator = new MiniBatchIterator(settings, sampleList);
            while (miniBatchIterator.hasNext()) {
                List<NeuralSample> minibatch = miniBatchIterator.next();
                List<Result> results = minibatchParallelLearn(neuralModel, minibatch);
                resultList.addAll(results);
            }
            return resultList;
        }

        @Override
        public List<Result> evaluate(List<NeuralSample> trainingSet) {
            List<Result> resultList = new ArrayList<>(trainingSet.size());
            MiniBatchIterator miniBatchIterator = new MiniBatchIterator(settings, trainingSet);
            while (miniBatchIterator.hasNext()) {
                List<NeuralSample> minibatch = miniBatchIterator.next();
                List<Result> results = minibatchParallelEvaluate(minibatch);
                resultList.addAll(results);
            }
            return resultList;
        }

        @Override
        public void restart(Settings settings) {
            MiniBatchTrainer.this.optimizer.restart(settings);
        }

        @Override
        public void setupDebugger(NeuralDebugging trainingDebugger) {
            neuralDebugger = trainingDebugger;
        }
    }

    /**
     * Learning by iterating a given {@link Stream} of samples in minibatches.
     */
    public class MinibatchStreamTrainer implements StreamTrainer {

        /**
         * The input stream should not be parallel otherwise the batches will be processed in parallel which is not correct.
         *
         * @param neuralModel
         * @param sampleStream
         * @return
         */
        @Override
        public Stream<Result> learnEpoch(NeuralModel neuralModel, Stream<NeuralSample> sampleStream) {
            if (sampleStream.isParallel()) {
                LOG.severe("The input sampleStream is parallel, but the training must perform sequential gradient steps!");
            }
            Stream<List<NeuralSample>> minibatchStream = StreamSupport.stream(new Utilities.BatchSpliterator<>(sampleStream.spliterator(), minibatchSize), false);  //todo test this crazy thing
            Stream<Result> resultStream = minibatchStream.map(batch -> minibatchParallelLearn(neuralModel, batch)).flatMap(List::stream);
            return resultStream;
        }

        @Override
        public void setupDebugger(NeuralDebugging trainingDebugger) {
            neuralDebugger = trainingDebugger;
        }
    }

    /**
     * Run batch of samples in parallel using the default {@link java.util.concurrent.ForkJoinPool} of the parallel stream (should be better than custom {@link java.util.concurrent.ExecutorService} ThreadPool)
     * as these are just CPU intensive operations with no IO waiting. Also the number of threads is not given by the size of the batch, but should rather be optimally selected w.r.t. hardware.
     * Finally leaving the parallelism to ParallelStream is also the most readable java8 solution, so lets just leave the magic to java
     *
     * @param neuralModel
     * @param sampleList
     * @return
     */
    private List<Result> minibatchParallelLearn(final NeuralModel neuralModel, List<NeuralSample> sampleList) {
        List<Result> results = new ArrayList<>(sampleList.size());
        if (sampleList.size() > minibatchSize) {
            LOG.severe("Minibatch size mismatch");
        }
        List<Result> resultList = IntStream.range(0, minibatchSize).parallel().mapToObj(i -> new Task(trainers.get(i), sampleList.get(i))).map(task -> task.runLearning(neuralModel)).collect(Collectors.toList());
        return resultList;
    }

    private List<Result> minibatchParallelEvaluate(List<NeuralSample> minibatch) {
        List<Result> results = new ArrayList<>(minibatch.size());
        if (minibatch.size() > minibatchSize) {
            LOG.severe("Minibatch size mismatch");
        }
        List<Result> resultList = IntStream.range(0, minibatchSize).parallel().mapToObj(i -> new Task(trainers.get(i), minibatch.get(i))).map(task -> task.runEvaluation()).collect(Collectors.toList());
        return resultList;
    }


    /**
     * Single sample training task object encompassing the necessary resources. Used for parallel execution.
     */
    private class Task {
        SequentialTrainer trainer;
        NeuralSample sample;

        public Task(SequentialTrainer trainer, NeuralSample sample) {
            this.trainer = trainer;
            this.sample = sample;
        }

        public Result runLearning(NeuralModel neuralModel) {
            return learnFromSample(neuralModel, sample, trainer.dropout, trainer.invalidation, trainer.evaluation, trainer.backpropagation);
        }

        public Result runEvaluation() {
            invalidateSample(trainer.invalidation, sample);
            return evaluateSample(trainer.evaluation, sample);
        }
    }

    /**
     * Custom iterator to iteratively return minibatches from a list of samples
     */
    public class MiniBatchIterator implements Iterator<List<NeuralSample>> {
        List<NeuralSample> sampleList;
        int i = 0;
        int minibatchSize;

        public MiniBatchIterator(Settings settings, List<NeuralSample> sampleList) {
            this.minibatchSize = settings.minibatchSize;
            this.sampleList = sampleList;
        }

        @Override
        public boolean hasNext() {
            return i < sampleList.size();
        }

        @Override
        public List<NeuralSample> next() {
            List<NeuralSample> neuralSamples = sampleList.subList(i, Math.min(i + minibatchSize, sampleList.size()));
            i += minibatchSize;
            return neuralSamples;
        }
    }
}
