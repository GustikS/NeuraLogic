package training;

import training.results.Results;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Training {
    Results results;


    void initTraining() {
    }

    abstract void train(NeuralModel model, List<NeuralSample> sampleList);

    Results finishTraining() {
        return null;
    }
}