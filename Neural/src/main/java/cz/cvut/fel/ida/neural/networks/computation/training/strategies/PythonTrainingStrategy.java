package cz.cvut.fel.ida.neural.networks.computation.training.strategies;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.learning.results.Progress;
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

    @Override
    public void export(Exporter exporter) {
    }

    @Override
    public String exportToJson() {
        return null;
    }
}