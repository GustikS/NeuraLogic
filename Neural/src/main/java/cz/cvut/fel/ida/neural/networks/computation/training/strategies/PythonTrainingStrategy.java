package cz.cvut.fel.ida.neural.networks.computation.training.strategies;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.PythonEvaluation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.PythonHookHandler;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.optimizers.Optimizer;
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

    ValueInitializer valueInitializer;

    PythonEvaluation evaluation;

    public PythonTrainingStrategy(Settings settings, NeuralModel model) {
        super(settings, model);

        this.trainer = new SequentialTrainer(settings, Optimizer.getFrom(settings, learningRate), currentModel);
        this.listTrainer = this.trainer.new SequentialListTrainer();
        this.valueInitializer = ValueInitializer.getInitializer(settings);

        evaluation = new PythonEvaluation(settings, -1);
        this.trainer.setEvaluation(evaluation);
    }

    public void setHooks(Set<String> hooks, PythonHookHandler callback) {
        evaluation.hooks = hooks;
        evaluation.hookHandler = callback;
    }

    public void setSamples(List<NeuralSample> samples) {
        this.samplesSet = samples;
    }

    public void resetParameters() {
        listTrainer.restart(settings);
        currentModel.resetWeights(valueInitializer);
    }

    @Override
    public Pair<NeuralModel, Progress> train() {
        return null;
    }

    @Override
    public void setupDebugger(NeuralDebugging neuralDebugger) { }

    public String learnSamples(int epochs) {
        return learnSamples(samplesSet, epochs);
    }

    public String learnSamples(List<NeuralSample> samples, int epochs) {
        List<Result> results = null;

        if (epochs <= 0) {
            return "[]";
        }

        for (int i = 0; i < epochs; i++) {
            results = listTrainer.learnEpoch(currentModel, samples);
        }

        List<String> output = new ArrayList<>();
        NumberFormat format = Settings.superDetailedNumberFormat;

        for (int i = 0, j = results.size(); i < j; ++i) {
            Result result = results.get(i);

            output.add(Arrays.toString(new String[] {
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

    public String evaluateSamples(List<NeuralSample> samples) {
        List<String> output = new ArrayList<>();
        NumberFormat format = Settings.superDetailedNumberFormat;

        for (int i = 0, j = samples.size(); i < j; ++i) {
            output.add(evaluation.evaluate(samples.get(i).query).toString(format));
        }

        return output.toString();
    }

    class PythonSampleBackpropagateLoss {
        private final Result result;

        private final NeuralSample sample;

        PythonSampleBackpropagateLoss(NeuralSample sample, Result result) {
            this.sample = sample;
            this.result = result;
        }

        public void backward() {
            WeightUpdater weightUpdater = trainer.backpropSample(trainer.getBackpropagation(), result, sample);
            trainer.updateWeights(currentModel, weightUpdater);
        }

        public Value getOutput() {
            return this.result.getOutput();
        }

        public Value getTarget() {
            return this.result.getTarget();
        }

        public Value getError() {
            return this.result.errorValue();
        }
    }

    @Override
    public void export(Exporter exporter) { }

    @Override
    public String exportToJson() {
        return null;
    }
}