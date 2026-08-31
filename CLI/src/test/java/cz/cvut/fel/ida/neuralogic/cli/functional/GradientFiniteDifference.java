package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.optimizers.Optimizer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.SequentialTrainer;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.building.End2endTrainigBuilder;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The backward pass has to agree with the slope of the forward pass it belongs to. Nothing else is needed to
 * catch a wrong gradient: if moving a weight changes the loss at a different rate than the gradient claims,
 * the gradient is wrong, whatever the conventions.
 * <p>
 * The gradient is read straight out of the {@link WeightUpdater}, so unlike the same check driven through the
 * Python bindings this one needs no assumption about the optimizer - it never takes a step.
 */
public class GradientFiniteDifference {
    private static final Logger LOG = Logger.getLogger(GradientFiniteDifference.class.getName());

    private static final double STEP = 1e-4;

    @TestAnnotations.Fast
    public void gradientAgreesWithACentralDifferenceOfTheLoss() throws Exception {
        Settings settings = settings();
        Pair<NeuralModel, List<NeuralSample>> built = build(settings);
        NeuralModel model = built.r;
        List<NeuralSample> samples = built.s;

        double[] start = weights(model);
        assertTrue(start.length > 0, "the dataset has to give the model something learnable to differentiate");

        double[] analytic = gradient(settings, model, samples);

        for (int i = 0; i < start.length; i++) {
            double forward = lossWithShift(settings, model, samples, start, i, +STEP);
            double backward = lossWithShift(settings, model, samples, start, i, -STEP);
            double numeric = (forward - backward) / (2 * STEP);
            assertEquals(numeric, analytic[i], 1e-5, "weight " + i);
        }
        setWeights(model, start);
    }

    /**
     * Summed over the samples, so it is the gradient of the same quantity {@link #lossWithShift} measures.
     * <p>
     * Note the sign: what the updater holds is the descent direction rather than the derivative, because
     * {@link cz.cvut.fel.ida.neural.networks.computation.training.optimizers.SGD} <em>adds</em>
     * <code>learningRate * update</code> to the weight. So it is negated here to be compared against
     * <code>dL/dw</code>. Reading it as the gradient without that flip is an easy mistake to make.
     * {@link cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Backpropagation#backpropagate}
     * clears its updates each call, hence the accumulation here.
     */
    private static double[] gradient(Settings settings, NeuralModel model, List<NeuralSample> samples) {
        SequentialTrainer trainer =
                new SequentialTrainer(settings, Optimizer.getFrom(settings, new ScalarValue(0.0)), model);
        double[] total = new double[weights(model).length];

        for (NeuralSample sample : samples) {
            trainer.invalidateSample(trainer.getInvalidation(), sample);
            Result result = trainer.evaluateSample(trainer.getEvaluation(), sample);
            WeightUpdater updater = trainer.getBackpropagation().backpropagate(sample, result);

            int offset = 0;     //the same flat order as weights()
            for (Weight weight : model.learnableWeights) {
                Value update = weight.index >= 0 && weight.index < updater.weightUpdates.length
                        ? updater.weightUpdates[weight.index]
                        : null;
                int size = 0;
                for (Double ignored : weight.value) {
                    size++;
                }
                if (update != null) {
                    int i = 0;
                    for (Double value : update) {
                        total[offset + i++] -= value;   //descent direction to derivative
                    }
                }
                offset += size;
            }
        }
        return total;
    }

    private static double lossWithShift(
            Settings settings, NeuralModel model, List<NeuralSample> samples, double[] start, int position, double shift) {
        double[] shifted = start.clone();
        shifted[position] += shift;
        setWeights(model, shifted);

        SequentialTrainer trainer =
                new SequentialTrainer(settings, Optimizer.getFrom(settings, new ScalarValue(0.0)), model);
        double total = 0;
        for (NeuralSample sample : samples) {
            trainer.invalidateSample(trainer.getInvalidation(), sample);
            Result result = trainer.evaluateSample(trainer.getEvaluation(), sample);
            for (Double value : result.errorValue()) {
                total += value;
            }
        }
        return total;
    }

    private static Settings settings() {
        Settings settings = Settings.forFastTest();
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        settings.errorFunction = Settings.ErrorFcn.SQUARED_DIFF;
        settings.inferOutputFcns = false;
        settings.squishLastLayer = false;
        settings.infer();
        return settings;
    }

    private static Pair<NeuralModel, List<NeuralSample>> build(Settings settings) throws Exception {
        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);
        Pair<String, Pair<NeuralModel, Stream<NeuralSample>>> result =
                new End2endTrainigBuilder(settings, sources).new End2endNNBuilder().buildPipeline().execute(sources);
        return new Pair<>(result.s.r, result.s.s.collect(Collectors.toList()));
    }

    private static double[] weights(NeuralModel model) {
        List<Double> values = new ArrayList<>();
        for (Weight weight : model.learnableWeights) {
            for (Double value : weight.value) {
                values.add(value);
            }
        }
        double[] flat = new double[values.size()];
        for (int i = 0; i < flat.length; i++) {
            flat[i] = values.get(i);
        }
        return flat;
    }

    private static void setWeights(NeuralModel model, double[] values) {
        int next = 0;
        for (Weight weight : model.learnableWeights) {
            int i = 0;
            for (Double ignored : weight.value) {
                weight.value.set(i++, values[next++]);
            }
        }
    }
}
