package networks.computation.training.trainingStrategies;

import networks.computation.evaluation.Evaluation;
import networks.computation.evaluation.results.Result;
import networks.computation.iteration.actions.WeightUpdater;
import networks.computation.training.Backpropagation;
import networks.computation.iteration.IndependentNeuronProcessing;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.optimizers.Optimizer;
import settings.Settings;

import java.util.logging.Logger;

public class Trainer {
    private static final Logger LOG = Logger.getLogger(Trainer.class.getName());

    Settings settings;

    Optimizer optimizer;

    Result learnFromSample(NeuralModel neuralModel, NeuralSample neuralSample, IndependentNeuronProcessing dropouter, IndependentNeuronProcessing invalidation, Evaluation evaluation, Backpropagation backpropagation) {
        if (settings.dropoutMode == Settings.DropoutMode.DROPOUT && settings.dropoutRate > 0) {
            dropoutSample(dropouter, neuralSample);
        }
        invalidateSample(invalidation, neuralSample);
        Result result = evaluateSample(evaluation, neuralSample);
        WeightUpdater weightUpdater = backpropSample(backpropagation, result, neuralSample);
        updateWeights(neuralModel, weightUpdater);
        return result;
    }

    void dropoutSample(IndependentNeuronProcessing dropouter, NeuralSample neuralSample) {
        dropouter.process(neuralSample.query.evidence, neuralSample.query.neuron);
    }

    void invalidateSample(IndependentNeuronProcessing invalidation, NeuralSample neuralSample) {
        invalidation.process(neuralSample.query.evidence, neuralSample.query.neuron);
    }

    Result evaluateSample(Evaluation evaluation, NeuralSample neuralSample) {
        return evaluation.evaluate(neuralSample);
    }

    WeightUpdater backpropSample(Backpropagation backpropagation, Result evaluatedResult, NeuralSample neuralSample) {
        return backpropagation.backpropagate(neuralSample, evaluatedResult);
    }

    void updateWeights(NeuralModel model, WeightUpdater weightUpdater) {
        optimizer.performGradientStep(model, weightUpdater);
    }
}
