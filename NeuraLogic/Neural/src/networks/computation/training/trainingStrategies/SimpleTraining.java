package networks.computation.training.trainingStrategies;

import networks.computation.evaluation.Evaluation;
import networks.computation.evaluation.results.Result;
import networks.computation.training.Backpropagation;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import settings.Settings;

import java.util.ArrayList;
import java.util.Collections;
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

    protected void initEpoch(int epochNumber) {
        super.initEpoch(epochNumber);
        if (settings.alwaysShuffle) {
            Collections.shuffle(sampleList);
        }
    }

    @Override
    protected List<Result> learnEpoch(int epochNumber) {
        List<Result> resultList = new ArrayList<>(sampleList.size());
        for (NeuralSample neuralSample : sampleList) {
            Result evaluation = this.evaluation.evaluate(currentModel, neuralSample);
            resultList.add(evaluation);

            backpropagation.backpropagate(currentModel, evaluation, neuralSample.query);
        }
        return resultList;
    }

}
