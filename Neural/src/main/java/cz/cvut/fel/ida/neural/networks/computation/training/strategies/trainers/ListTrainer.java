package cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers;

import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;

/**
 * A single epoch (iteration over all the samples) necessary actions for training.
 */
public interface ListTrainer extends Training{

    /**
     * Learning of an i-th epoch, i.e. invalidation, evaluation and backprop over all the samples
     *
     * @return
     */
    List<Result> learnEpoch(NeuralModel neuralModel, List<NeuralSample> sampleList);

    List<Result> evaluate(List<NeuralSample> trainingSet);

    void restart(Settings settings);

}