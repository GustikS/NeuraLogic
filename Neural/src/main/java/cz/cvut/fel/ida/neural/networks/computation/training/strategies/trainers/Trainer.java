package cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers;

import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Backpropagation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Evaluation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.IndependentNeuronProcessing;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.GradientClipping;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.optimizers.Optimizer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging.NeuralDebugging;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Collections;
import java.util.List;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.logging.Logger;

public class Trainer implements Exportable {
    private static final Logger LOG = Logger.getLogger(Trainer.class.getName());

    protected Settings settings;

    /**
     * For parallel access to shared neurons
     */
    int index;

    /**
     * Current iteration number
     */
    int iterationNumber;

    transient Optimizer optimizer;

    public NeuralDebugging neuralDebugger;

    public Trainer(Settings settings, Optimizer optimizer) {
        this.settings = settings;
        this.optimizer = optimizer;
        this.iterationNumber = 0;
//        this.neuralDebugger = new NeuralDebugger(settings);
    }

    public Trainer() {
    }

    protected Result learnFromSample(NeuralModel neuralModel, NeuralSample neuralSample, IndependentNeuronProcessing dropouter, IndependentNeuronProcessing invalidation, Evaluation evaluation, Backpropagation backpropagation) {
        if (settings.dropoutMode == Settings.DropoutMode.DROPOUT && settings.dropoutRate > 0) {
            dropoutSample(dropouter, neuralSample);
        }
        invalidateSample(invalidation, neuralSample);   //todo check why there is a huge number at init for outputValue - is it?
        Result result = evaluateSample(evaluation, neuralSample);
        WeightUpdater weightUpdater = backpropSample(backpropagation, result, neuralSample);
        reduceAndClip(weightUpdater, Collections.singletonList(result));
        updateWeights(neuralModel, weightUpdater);
        if (settings.debugSampleTraining) {
            neuralDebugger.debug(neuralSample);
        }
        return result;
    }

    /**
     * Everything torch does between <code>backward()</code> and <code>step()</code>, in torch's order: the
     * loss reduction reaches the gradient as a scale, and then the gradient is clipped.
     * <p>
     * In one place because a descending path that skips it descends a different function than the one that
     * was reported, and the same model would then train differently depending on which entry point was
     * called - which it did: <code>learnSample</code> was outside this and so ignored the reduction entirely.
     * <p>
     * A batch of one still needs the reduction: MEAN divides by that sample's importance times its target
     * width, torch's N x C with N = 1. At a scalar target that is 1.0 and nothing moves.
     */
    public void reduceAndClip(WeightUpdater weightUpdater, List<Result> batch) {
        weightUpdater.scaleUpdates(1.0 / Result.reductionDivisor(batch, settings.errorReduction));
        GradientClipping.clip(weightUpdater.weightUpdates, settings);
    }

    void dropoutSample(IndependentNeuronProcessing dropouter, NeuralSample neuralSample) {
        dropouter.process(neuralSample.query.evidence, neuralSample.query.neuron);
    }

    public void invalidateSample(IndependentNeuronProcessing invalidation, NeuralSample neuralSample) {
        neuralSample.query.evidence.initializeStatesCache(index);    //here we can transfer information from Structure to Computation
        invalidation.process(neuralSample.query.evidence, neuralSample.query.neuron);
    }

    public Result evaluateSample(Evaluation evaluation, NeuralSample neuralSample) {
        return evaluation.evaluate(neuralSample);
    }

    public WeightUpdater backpropSample(Backpropagation backpropagation, Result evaluatedResult, NeuralSample neuralSample) {
        return backpropagation.backpropagate(neuralSample, evaluatedResult);
    }

    /**
     * todo test remove synchronized for speedup?
     *
     * @param model
     * @param weightUpdater
     */
    synchronized public void updateWeights(NeuralModel model, WeightUpdater weightUpdater) {
        optimizer.performGradientStep(model, weightUpdater, ++iterationNumber);
    }

    public void restart() {
        optimizer.restart(settings);
    }
}
