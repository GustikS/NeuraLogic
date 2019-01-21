package networks.computation.training.trainingStrategies;

import networks.computation.evaluation.results.Result;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;

import java.util.stream.Stream;

public interface StreamTrainer {

    /**
     * Learning of an epoch as a single pass, i.e. invalidation, evaluation and backprop over all the samples
     * @param neuralModel
     * @param sampleStream
     * @return
     */
    abstract Stream<Result> learnEpoch(NeuralModel neuralModel, Stream<NeuralSample> sampleStream);
}
