package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.computation.training.optimizers.Optimizer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.MiniBatchTrainer;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers.SequentialTrainer;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.building.End2endTrainigBuilder;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The two properties a minibatch has to keep: under plain SGD its update is what its samples would have done
 * on their own, reduced the way the error function says, and evaluating it does not train anything.
 * <p>
 * The reduction is pinned in each test rather than left to the default, because it is exactly what decides
 * between the two: SUM adds the samples' updates, MEAN divides that by the batch's total element count, as
 * torch does. These targets are scalars, so that count is the batch size.
 */
public class MinibatchTraining {
    private static final Logger LOG = Logger.getLogger(MinibatchTraining.class.getName());

    private static final Value LEARNING_RATE = new ScalarValue(0.05);

    @TestAnnotations.Fast
    public void batchUpdateIsTheSumOfTheSampleUpdates() throws Exception {
        assertBatchAgainstItsSamples(Settings.ErrorReduction.SUM);
    }

    /**
     * The same thing under MEAN, where torch divides by the element count - here the batch size, the targets
     * being scalars. Both are asserted so that neither convention can drift without saying so.
     */
    @TestAnnotations.Fast
    public void batchUpdateIsTheMeanOfTheSampleUpdatesUnderMeanReduction() throws Exception {
        assertBatchAgainstItsSamples(Settings.ErrorReduction.MEAN);
    }

    private void assertBatchAgainstItsSamples(Settings.ErrorReduction reduction) throws Exception {
        Settings settings = sgdSettings();
        settings.errorReduction = reduction;
        Pair<NeuralModel, List<NeuralSample>> built = build(settings);
        NeuralModel model = built.r;
        List<NeuralSample> batch = built.s;

        double[] initial = weights(model);
        double[] summed = new double[initial.length];

        for (NeuralSample sample : batch) {
            setWeights(model, initial);
            new SequentialTrainer(settings, Optimizer.getFrom(settings, LEARNING_RATE), model)
                    .new SequentialListTrainer().learnEpoch(model, Collections.singletonList(sample));
            double[] alone = weights(model);
            for (int i = 0; i < summed.length; i++) {
                summed[i] += alone[i] - initial[i];
            }
        }

        setWeights(model, initial);
        new MiniBatchTrainer(settings, Optimizer.getFrom(settings, LEARNING_RATE), model, batch.size())
                .new MinibatchListTrainer().learnEpoch(model, batch);
        double[] batched = weights(model);

        // the divisor is the batch's total element count, and these targets are scalars
        double expectedRatio = reduction == Settings.ErrorReduction.MEAN ? 1.0 / batch.size() : 1.0;
        for (int i = 0; i < initial.length; i++) {
            assertEquals(summed[i] * expectedRatio, batched[i] - initial[i], 1e-12, "weight " + i);
        }
    }

    @TestAnnotations.Fast
    public void evaluatingABatchDoesNotChangeWeights() throws Exception {
        Settings settings = sgdSettings();
        Pair<NeuralModel, List<NeuralSample>> built = build(settings);
        NeuralModel model = built.r;

        double[] before = weights(model);
        new MiniBatchTrainer(settings, Optimizer.getFrom(settings, LEARNING_RATE), model, built.s.size())
                .new MinibatchListTrainer().evaluate(built.s);

        assertArrayEquals(before, weights(model));
    }

    /**
     * The sequential trainer skips a sample with no query neuron, and the neuralizer does produce them. Here it
     * used to throw. Two epochs, because the trainer that draws the skipped sample must not carry the previous
     * epoch's updates into the batch sum either.
     */
    @TestAnnotations.Fast
    public void aSampleWithoutAQueryNeuronIsSkippedRatherThanFatal() throws Exception {
        Settings settings = sgdSettings();
        Pair<NeuralModel, List<NeuralSample>> built = build(settings);
        NeuralModel model = built.r;
        List<NeuralSample> batch = built.s;

        double[] initial = weights(model);
        double[] withoutIt = trainTwice(settings, model, initial, batch);

        List<NeuralSample> withHeadless = new ArrayList<>(batch);
        withHeadless.add(headlessSample(batch.get(0)));
        double[] withIt = trainTwice(settings, model, initial, withHeadless);

        assertArrayEquals(withoutIt, withIt, "a sample with no query neuron has to contribute nothing at all");
    }

    private static NeuralSample headlessSample(NeuralSample like) {
        QueryNeuron headless = new QueryNeuron("noQueryHead", 0, 1.0, null, like.query.evidence);
        return new NeuralSample(new ScalarValue(1.0), headless, like.type);
    }

    private static double[] trainTwice(Settings settings, NeuralModel model, double[] initial, List<NeuralSample> batch) {
        setWeights(model, initial);
        MiniBatchTrainer.MinibatchListTrainer trainer =
                new MiniBatchTrainer(settings, Optimizer.getFrom(settings, LEARNING_RATE), model, batch.size())
                        .new MinibatchListTrainer();
        trainer.learnEpoch(model, batch);
        trainer.learnEpoch(model, batch);
        return weights(model);
    }

    private static Settings sgdSettings() {
        Settings settings = Settings.forFastTest();
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        return settings;
    }

    private static Pair<NeuralModel, List<NeuralSample>> build(Settings settings) throws Exception {
        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);
        Pair<String, Pair<NeuralModel, Stream<NeuralSample>>> result =
                new End2endTrainigBuilder(settings, sources).new End2endNNBuilder().buildPipeline().execute(sources);

        List<NeuralSample> samples = result.s.s.collect(Collectors.toList());
        assertTrue(samples.size() > 1, "the dataset must give more than one sample for a batch to mean anything");
        return new Pair<>(result.s.r, samples);
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
