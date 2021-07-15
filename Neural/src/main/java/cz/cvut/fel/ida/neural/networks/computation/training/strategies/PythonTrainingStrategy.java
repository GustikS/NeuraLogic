package cz.cvut.fel.ida.neural.networks.computation.training.strategies;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Evaluation;
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

    public double learnSamples(int epochs) {
        return learnSamples(samplesSet, epochs);
    }

    public double learnSamples(List<NeuralSample> samples, int epochs) {
        double totalError = 0.;
        List<Result> results = null;

        for (int i = 0; i < epochs; i++) {
            results = listTrainer.learnEpoch(currentModel, samples);
        }

        for (int i = results.size() - 1; i >= 0; --i) {
            totalError += ((ScalarValue) results.get(i).errorValue()).value;
        }

        return totalError;
    }

    public double learnSample(NeuralSample sample) {
        trainer.invalidateSample(trainer.getInvalidation(), sample);
        Result result = trainer.evaluateSample(trainer.getEvaluation(), sample);

        WeightUpdater weightUpdater = trainer.backpropSample(trainer.getBackpropagation(), result, sample);
        trainer.updateWeights(currentModel, weightUpdater);
        return ((ScalarValue) result.errorValue()).value;
    }

    public PythonSampleBackpropagateLoss evaluateSample(NeuralSample sample) {
        trainer.invalidateSample(trainer.getInvalidation(), sample);
        Result result = trainer.evaluateSample(trainer.getEvaluation(), sample);

        return new PythonSampleBackpropagateLoss(sample, result);
    }

    public List<Result> evaluateSamples(List<NeuralSample> samples) {
        return listTrainer.evaluate(samples);
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