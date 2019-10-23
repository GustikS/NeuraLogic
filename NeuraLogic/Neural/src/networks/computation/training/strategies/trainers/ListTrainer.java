package networks.computation.training.strategies.trainers;

import networks.computation.evaluation.results.Result;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import settings.Settings;

import java.util.List;

/**
 * A single epoch (iteration over all the samples) necessary actions for training.
 */
public interface ListTrainer {

    /**
     * Learning of an i-th epoch, i.e. invalidation, evaluation and backprop over all the samples
     *
     * @return
     */
    List<Result> learnEpoch(NeuralModel neuralModel, List<NeuralSample> sampleList);

    List<Result> evaluate(List<NeuralSample> trainingSet);

    void restart(Settings settings);
}