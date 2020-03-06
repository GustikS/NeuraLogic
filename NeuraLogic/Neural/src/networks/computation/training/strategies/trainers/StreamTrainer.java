package networks.computation.training.strategies.trainers;

import learning.results.Result;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;

import java.util.stream.Stream;

public interface StreamTrainer extends Training{

    /**
     * Learning of an epoch as a single pass, i.e. invalidation, evaluation and backprop over all the samples
     * @param neuralModel
     * @param sampleStream
     * @return
     */
    Stream<Result> learnEpoch(NeuralModel neuralModel, Stream<NeuralSample> sampleStream);
}
