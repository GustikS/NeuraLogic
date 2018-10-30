package networks.computation.training.trainingStrategies;

import networks.computation.evaluation.Evaluation;
import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.results.Results;
import networks.computation.training.Backpropagation;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import settings.Settings;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class SimpleTraining extends TrainingStrategy {

    public SimpleTraining(Settings settings, NeuralModel model, List<NeuralSample> sampleList, Evaluation evaluation, Backpropagation backpropagation) {
        super(settings, model, sampleList, evaluation, backpropagation);
    }

    public SimpleTraining(Settings settings, NeuralModel model, List<NeuralSample> sampleList) {
        super(settings, model, sampleList);
    }

    @Override
    protected void initTraining() {
        List<Result> results = new LinkedList<>();
        for (NeuralSample sample : sampleList) {
            Result evaluate = evaluation.evaluate(model, sample);
            results.add(evaluate);
        }
        progress.addNext(Results.getFrom(settings, results));
    }

    @Override
    protected void initEpoch() {
        if (settings.alwaysShuffle){
            Collections.shuffle(sampleList);
        }
    }

    @Override
    protected void learnEpoch() {

    }

    @Override
    protected void initRestart() {

    }


    @Override
    protected Results finish() {
        return null;
    }

    @Override
    public NeuralModel getBestModel() {
        return null;
    }
}
