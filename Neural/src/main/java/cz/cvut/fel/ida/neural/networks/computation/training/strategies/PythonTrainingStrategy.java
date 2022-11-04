package cz.cvut.fel.ida.neural.networks.computation.training.strategies;

import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.PythonEvaluation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.PythonHookHandler;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class PythonTrainingStrategy extends TrainingStrategy {

    transient List<NeuralSample> samplesSet;

    transient SequentialTrainer trainer;

    transient ListTrainer listTrainer;

    MiniBatchTrainer miniBatchTrainer;

    ListTrainer minibatchListTrainer;

    ValueInitializer valueInitializer;

    PythonEvaluation evaluation;

    LearnRateDecayStrategy learnRateDecay;

    int epochCount = 0;

    public PythonTrainingStrategy(Settings settings, NeuralModel model, Optimizer optimizer, LearnRateDecayStrategy learnRateDecay) {
        super(settings, model);

        this.trainer = new SequentialTrainer(settings, optimizer, currentModel);
        this.listTrainer = this.trainer.new SequentialListTrainer();
        this.valueInitializer = ValueInitializer.getInitializer(settings);

        evaluation = new PythonEvaluation(settings, -1);
        this.trainer.setEvaluation(evaluation);

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

    public void setHooks(Set<String> hooks, PythonHookHandler callback) {
        evaluation.hooks = hooks;
        evaluation.hookHandler = callback;
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
    public void setupDebugger(NeuralDebugging neuralDebugger) { }

    public String learnSamples(int epochs, int minibatchSize) {
        return learnSamples(samplesSet, epochs, minibatchSize);
    }

    public String learnSamples(List<NeuralSample> samples, int epochs, int minibatchSize) {
        List<Result> results = null;

        if (epochs <= 0) {
            return "[]";
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
        }

        List<String> output = new ArrayList<>(samples.size());
        NumberFormat format = Settings.superDetailedNumberFormat;

        for (Result result : results) {
            output.add(Arrays.toString(new String[]{
                    result.getTarget().toString(format),
                    result.getOutput().toString(format),
                    result.errorValue().toString(format),
            }));
        }

        return output.toString();
    }

    public String learnSample(NeuralSample sample) {
        trainer.invalidateSample(trainer.getInvalidation(), sample);
        Result result = trainer.evaluateSample(trainer.getEvaluation(), sample);

        WeightUpdater weightUpdater = trainer.backpropSample(trainer.getBackpropagation(), result, sample);
        trainer.updateWeights(currentModel, weightUpdater);
        NumberFormat format = Settings.superDetailedNumberFormat;

        return Arrays.toString(new String[]{
                result.getTarget().toString(format),
                result.getOutput().toString(format),
                result.errorValue().toString(format),
        });
    }

    public String evaluateSample(NeuralSample sample) {
        trainer.invalidateSample(trainer.getInvalidation(), sample);
        return evaluation.evaluate(sample.query).toString(Settings.superDetailedNumberFormat);
    }

    public String evaluateSamples(List<NeuralSample> samples, int minibatchSize) {
        List<String> output = new ArrayList<>(samples.size());
        NumberFormat format = Settings.superDetailedNumberFormat;

        if (minibatchSize > 1) {
            miniBatchTrainer.setMinibatchSize(minibatchSize);

            for (Result result : minibatchListTrainer.evaluate(samples)) {
                output.add(result.getOutput().toString(format));
            }

            return output.toString();
        }

        for (NeuralSample sample : samples) {
            trainer.invalidateSample(trainer.getInvalidation(), sample);
            output.add(evaluation.evaluate(sample.query).toString(format));
        }

        return output.toString();
    }

    @Override
    public void export(Exporter exporter) { }

    @Override
    public String exportToJson() {
        return null;
    }
}