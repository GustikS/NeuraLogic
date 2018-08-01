package training;

import networks.evaluation.results.Results;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Training {
    Results results;


    void initTraining() {
    }

    protected abstract void train(NeuralModel model, List<NeuralSample> sampleList);

    Results finishTraining() {
        return null;
    }
}