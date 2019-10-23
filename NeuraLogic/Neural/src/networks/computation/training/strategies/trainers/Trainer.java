package networks.computation.training.strategies.trainers;

import networks.computation.evaluation.results.Result;
import networks.computation.iteration.actions.Backpropagation;
import networks.computation.iteration.actions.Evaluation;
import networks.computation.iteration.actions.IndependentNeuronProcessing;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.optimizers.Optimizer;
import settings.Settings;

import java.util.logging.Logger;

public class Trainer {
    private static final Logger LOG = Logger.getLogger(Trainer.class.getName());

    protected Settings settings;

    /**
     * For parallel access to shared neurons
     */
    int index;

    Optimizer optimizer;

    public Trainer(Settings settings, Optimizer optimizer) {
        this.settings = settings;
        this.optimizer = optimizer;
    }

    public Trainer() {
    }

    protected Result learnFromSample(NeuralModel neuralModel, NeuralSample neuralSample, IndependentNeuronProcessing dropouter, IndependentNeuronProcessing invalidation, Evaluation evaluation, Backpropagation backpropagation) {
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

    public void invalidateSample(IndependentNeuronProcessing invalidation, NeuralSample neuralSample) {
        neuralSample.query.evidence.initializeStatesCache(index);    //here we can transfer information from Structure to Computation
        invalidation.process(neuralSample.query.evidence, neuralSample.query.neuron);
    }

    Result evaluateSample(Evaluation evaluation, NeuralSample neuralSample) {
        return evaluation.evaluate(neuralSample);
    }

    WeightUpdater backpropSample(Backpropagation backpropagation, Result evaluatedResult, NeuralSample neuralSample) {
        return backpropagation.backpropagate(neuralSample, evaluatedResult);
    }

    /**
     * todo test remove synchronized for speedup?
     *
     * @param model
     * @param weightUpdater
     */
    synchronized void updateWeights(NeuralModel model, WeightUpdater weightUpdater) {
        optimizer.performGradientStep(model, weightUpdater);
    }

    public void restart() {
        optimizer.restart(settings);
    }
}
