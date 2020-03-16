package cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers;

import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;

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
