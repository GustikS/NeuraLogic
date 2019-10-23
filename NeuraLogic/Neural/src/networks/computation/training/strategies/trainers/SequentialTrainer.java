package networks.computation.training.strategies.trainers;

import networks.computation.evaluation.results.Result;
import networks.computation.iteration.actions.Backpropagation;
import networks.computation.iteration.actions.Evaluation;
import networks.computation.iteration.actions.IndependentNeuronProcessing;
import networks.computation.iteration.visitors.states.neurons.Dropouter;
import networks.computation.iteration.visitors.states.neurons.Invalidator;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.optimizers.Optimizer;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by gusta on 8.3.17.
 * <p>
 * Simple training on a single example one-by-one. Contains subclasses for sequential training from an input List but also a Strem of samples!
 */
public class SequentialTrainer extends Trainer {
    private static final Logger LOG = Logger.getLogger(SequentialTrainer.class.getName());

    IndependentNeuronProcessing dropout;
    IndependentNeuronProcessing invalidation;
    Evaluation evaluation;
    Backpropagation backpropagation;

    public SequentialTrainer(Settings settings, Optimizer optimizer, NeuralModel neuralModel) {
        this(settings, optimizer, neuralModel, -1);
    }

    public SequentialTrainer(Settings settings, Optimizer optimizer, NeuralModel neuralModel, int index) {
        super(settings, optimizer);
        this.index = index;
        evaluation = new Evaluation(settings, index);
        backpropagation = new Backpropagation(settings, neuralModel, index);
        invalidation = new IndependentNeuronProcessing(settings, new Invalidator(index));
        dropout = new IndependentNeuronProcessing(settings, new Dropouter(settings, index));
    }

    protected SequentialTrainer() {
    }

    public class SequentialListTrainer implements ListTrainer {

        @Override
        public List<Result> learnEpoch(NeuralModel neuralModel, List<NeuralSample> sampleList) {
            List<Result> resultList = new ArrayList<>(sampleList.size());
            for (NeuralSample neuralSample : sampleList) {
                Result result = learnFromSample(neuralModel, neuralSample, dropout, invalidation, evaluation, backpropagation);
                resultList.add(result);
            }
            return resultList;
        }

        @Override
        public List<Result> evaluate(List<NeuralSample> trainingSet) {
            List<Result> resultList = new ArrayList<>(trainingSet.size());
            for (NeuralSample neuralSample : trainingSet) {
                invalidateSample(invalidation, neuralSample);
                Result result = evaluateSample(evaluation, neuralSample);
                LOG.finest(neuralSample + " : " + result);
                resultList.add(result);
            }
            return resultList;
        }

        @Override
        public void restart(Settings settings) {
            SequentialTrainer.this.optimizer.restart(settings);
        }
    }

    public class SequentialStreamTrainer implements StreamTrainer {

        @Override
        public Stream<Result> learnEpoch(NeuralModel neuralModel, Stream<NeuralSample> sampleStream) {
            Stream<Result> resultStream = sampleStream.map(sample -> learnFromSample(neuralModel, sample, dropout, invalidation, evaluation, backpropagation));
            return resultStream;
        }
    }
}
