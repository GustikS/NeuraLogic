package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Evaluation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.IndependentNeuronProcessing;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Invalidator;
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
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A query's importance is its weight for learning, so halving it has to halve what the sample moves the
 * weights by. It used to be stored on the Query and read by nothing.
 */
public class QueryImportance {
    private static final Logger LOG = Logger.getLogger(QueryImportance.class.getName());

    private static final Value LEARNING_RATE = new ScalarValue(0.05);

    @TestAnnotations.Fast
    public void halvingImportanceHalvesTheUpdate() throws Exception {
        Settings settings = Settings.forFastTest();
        settings.setOptimizer(Settings.OptimizerSet.SGD);

        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);
        Pair<String, Pair<NeuralModel, Stream<NeuralSample>>> built =
                new End2endTrainigBuilder(settings, sources).new End2endNNBuilder().buildPipeline().execute(sources);
        NeuralModel model = built.s.r;
        List<NeuralSample> samples = built.s.s.collect(Collectors.toList());
        assertTrue(!samples.isEmpty());

        NeuralSample sample = samples.get(0);
        double[] initial = weights(model);

        sample.query.importance = 1.0;
        double[] full = delta(settings, model, sample, initial);

        sample.query.importance = 0.5;
        double[] half = delta(settings, model, sample, initial);

        boolean moved = false;
        for (double value : full) {
            moved |= Math.abs(value) > 1e-9;
        }
        assertTrue(moved, "the full-importance step has to move the weights for this to mean anything");

        for (int i = 0; i < full.length; i++) {
            assertEquals(full[i] / 2, half[i], 1e-12, "weight " + i);
        }
    }

    @TestAnnotations.Fast
    public void halvingImportanceHalvesTheReportedError() throws Exception {
        Settings settings = Settings.forFastTest();
        settings.setOptimizer(Settings.OptimizerSet.SGD);

        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);
        Pair<String, Pair<NeuralModel, Stream<NeuralSample>>> built =
                new End2endTrainigBuilder(settings, sources).new End2endNNBuilder().buildPipeline().execute(sources);
        NeuralModel model = built.s.r;
        NeuralSample sample = built.s.s.collect(Collectors.toList()).get(0);

        Evaluation evaluation = new Evaluation(settings, -1);
        IndependentNeuronProcessing invalidation =
                new IndependentNeuronProcessing(settings, new Invalidator(-1));

        sample.query.importance = 1.0;
        invalidation.process(sample.query.evidence, sample.query.neuron);
        double full = scalar(evaluation.evaluate(sample).errorValue());

        sample.query.importance = 0.5;
        invalidation.process(sample.query.evidence, sample.query.neuron);
        double half = scalar(evaluation.evaluate(sample).errorValue());

        assertTrue(Math.abs(full) > 1e-9, "the sample has to have some error for this to mean anything");
        assertEquals(full / 2, half, 1e-12);
    }

    private static double scalar(Value value) {
        double result = 0;
        for (Double element : value) {
            result += element;
        }
        return result;
    }

    private static double[] delta(Settings settings, NeuralModel model, NeuralSample sample, double[] initial) {
        setWeights(model, initial);
        new SequentialTrainer(settings, Optimizer.getFrom(settings, LEARNING_RATE), model)
                .new SequentialListTrainer().learnEpoch(model, Collections.singletonList(sample));
        double[] after = weights(model);
        double[] delta = new double[initial.length];
        for (int i = 0; i < delta.length; i++) {
            delta[i] = after[i] - initial[i];
        }
        return delta;
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
