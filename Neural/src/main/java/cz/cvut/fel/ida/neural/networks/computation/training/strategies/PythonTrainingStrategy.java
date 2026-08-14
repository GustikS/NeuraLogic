package cz.cvut.fel.ida.neural.networks.computation.training.strategies;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Evaluation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.optimizers.Optimizer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters.LearnRateDecayStrategy;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging.NeuralDebugging;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.*;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exporter;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PythonTrainingStrategy extends TrainingStrategy {

    transient List<NeuralSample> samplesSet;

    transient SequentialTrainer trainer;

    transient ListTrainer listTrainer;

    MiniBatchTrainer miniBatchTrainer;

    ListTrainer minibatchListTrainer;

    ValueInitializer valueInitializer;

    Evaluation evaluation;

    LearnRateDecayStrategy learnRateDecay;

    int epochCount = 0;

    public PythonTrainingStrategy(Settings settings, NeuralModel model, Optimizer optimizer, LearnRateDecayStrategy learnRateDecay) {
        super(settings, model);

        this.trainer = new SequentialTrainer(settings, optimizer, currentModel);
        this.listTrainer = this.trainer.new SequentialListTrainer();
        this.valueInitializer = ValueInitializer.getInitializer(settings);
        this.evaluation = this.trainer.getEvaluation();

        this.miniBatchTrainer = new MiniBatchTrainer(settings, optimizer, currentModel, 0);
        this.minibatchListTrainer = this.miniBatchTrainer.new MinibatchListTrainer();

        this.learnRateDecay = learnRateDecay;
    }

    public SequentialTrainer getTrainer() {
        return this.trainer;
    }

    public NeuralModel getCurrentModel() {
        return this.currentModel;
    }

    public void setSamples(List<NeuralSample> samples) {
        this.samplesSet = samples;
    }

    public void resetParameters() {
        if (learnRateDecay != null) {
            learnRateDecay.restart();
        }

        epochCount = 0;
        listTrainer.restart(settings);
        currentModel.resetWeights(valueInitializer);
    }

    @Override
    public Pair<NeuralModel, Progress> train() {
        return null;
    }

    @Override
    public void setupDebugger(NeuralDebugging neuralDebugger) {
    }

    public List<Result> learnSamples(int epochs, int minibatchSize) {
        return learnSamples(samplesSet, epochs, minibatchSize);
    }

    public List<Result> learnSamples(List<NeuralSample> samples, int epochs, int minibatchSize) {
        List<Result> results = null;

        if (epochs <= 0) {
            return new ArrayList<>();
        }

        ListTrainer trainer = listTrainer;

        if (minibatchSize > 1) {
            miniBatchTrainer.setMinibatchSize(minibatchSize);
            trainer = minibatchListTrainer;
        }

        for (int i = 0; i < epochs; i++) {
            epochCount++;

            if (learnRateDecay != null) {
                learnRateDecay.decay(epochCount);
            }

            results = trainer.learnEpoch(currentModel, samples);

//            if (settings.checkNeuronSaturation) {
//                saturationCheck(samples);
//            }
        }

        return results;
    }

    public Result learnSample(NeuralSample sample) {
        trainer.invalidateSample(trainer.getInvalidation(), sample);
        Result result = trainer.evaluateSample(trainer.getEvaluation(), sample);

        WeightUpdater weightUpdater = trainer.backpropSample(trainer.getBackpropagation(), result, sample);
        //this reimplements Trainer.learnFromSample rather than calling it, so anything that path gains has to
        //be repeated here or the two descend different functions - which they did, until the reduction was
        //added to only one of them and a single sample under MEAN stepped as if under SUM
        trainer.reduceAndClip(weightUpdater, Collections.singletonList(result));
        trainer.updateWeights(currentModel, weightUpdater);

        return result;
    }

    public Value evaluateSample(NeuralSample sample) {
        trainer.invalidateSample(trainer.getInvalidation(), sample);
        return evaluation.evaluate(sample.query);
    }

    public List<Value> evaluateSamples(List<NeuralSample> samples, int minibatchSize) {
        List<Value> output = new ArrayList<>(samples.size());

        if (minibatchSize > 1) {
            miniBatchTrainer.setMinibatchSize(minibatchSize);

            for (Result result : minibatchListTrainer.evaluate(samples)) {
                output.add(result.getOutput());
            }

            return output;
        }

        for (NeuralSample sample : samples) {
            trainer.invalidateSample(trainer.getInvalidation(), sample);
            output.add(evaluation.evaluate(sample.query));
        }

        return output;
    }

    /**
     * The loss of a whole dataset, reduced the way the error function says - so the single number torch's
     * criterion would hand back, and the quantity the optimizer is descending.
     * <p>
     * The per-sample values from {@link #validateSamples} are deliberately *not* reduced across the batch;
     * they are each summed over their own components, which is torch's reduction="none". This is the one
     * that applies the batch reduction, through the same {@link Result#reductionDivisor} the trainers scale
     * the gradient by, so the reported loss and the descended one cannot drift apart.
     */
    public Value reducedError(List<NeuralSample> samples, int minibatchSize) {
        List<Result> results = validateSamples(samples, minibatchSize);
        double total = 0;
        for (Result result : results) {
            for (double component : result.errorValue()) {
                total += component;
            }
        }
        return new ScalarValue(total / Result.reductionDivisor(results, settings.errorReduction));
    }

    public Result validateSample(NeuralSample sample) {
        trainer.invalidateSample(trainer.getInvalidation(), sample);
        return trainer.evaluateSample(evaluation, sample);
    }

    public List<Result> validateSamples(List<NeuralSample> samples, int minibatchSize) {
        List<Result> output = new ArrayList<>(samples.size());

        if (minibatchSize > 1) {
            miniBatchTrainer.setMinibatchSize(minibatchSize);

            for (Result result : minibatchListTrainer.evaluate(samples)) {
                output.add(result);
            }

            return output;
        }

        for (NeuralSample sample : samples) {
            trainer.invalidateSample(trainer.getInvalidation(), sample);
            output.add(trainer.evaluateSample(evaluation, sample));
        }

        return output;
    }

    @Override
    public void export(Exporter exporter) {
    }

    @Override
    public String exportToJson() {
        return null;
    }
}