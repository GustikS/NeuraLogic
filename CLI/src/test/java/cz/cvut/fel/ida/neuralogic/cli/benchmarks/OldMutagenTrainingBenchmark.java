package cz.cvut.fel.ida.neuralogic.cli.benchmarks;

import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.building.End2endTrainigBuilder;
import cz.cvut.fel.ida.pipelines.building.TrainingBuilder;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.junit.jupiter.api.Nested;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.RunnerException;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.cvut.fel.ida.utils.generic.Benchmarking.assertSmallRuntimeDeviation;
import static cz.cvut.fel.ida.utils.generic.Benchmarking.benchmarkSlow;
import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

@Nested
@TestAnnotations.Slow
public class OldMutagenTrainingBenchmark {

    static String[] dataset = getDatasetArgs("relational/molecules/mutagenesis");
    static int learningSteps = 100;
    static Settings.OptimizerSet optimizer = Settings.OptimizerSet.ADAM;



    private static final Logger LOG = Logger.getLogger(OldMutagenTrainingBenchmark.class.getName());

    @TestAnnotations.Slow
    public void testTrainingFastEnough() throws RunnerException {
        Duration referenceTime = Duration.ofSeconds(52);
        double maxDeviation = 0.1;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".defaultTraining", 3, 1);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    /**
     * Prepare the grounded and neuralized networks here for all the tests!
     */
    @State(Scope.Thread)
    public static class BenchmarkState {
        Sources sources;

        NeuralModel neuralModel;
        List<NeuralSample> samples;

        public static Settings settings;

        @Setup(Level.Trial)
        public void initialize() throws Exception {
            Logging.initLogging(settings);
            LOG.warning("Initializing state!");
            Settings settings = Settings.forSlowTest();
            settings.appLimitSamples = -1;
            settings.intermediateDebug = false;
            settings.debugExporting = false;
            settings.chainPruning = true;
            settings.isoValueCompression = true;
            settings.setOptimizer(optimizer);

            sources = Runner.getSources(dataset, settings);
            End2endTrainigBuilder.End2endNNBuilder end2endNNBuilder = new End2endTrainigBuilder(settings, sources).new End2endNNBuilder();
            Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> end2EndNNbuilding = end2endNNBuilder.buildPipeline();
            Pair<String, Pair<NeuralModel, Stream<NeuralSample>>> stringPairPair = end2EndNNbuilding.execute(sources);

            samples = stringPairPair.s.s.collect(Collectors.toList());
            neuralModel = stringPairPair.s.r;
        }
    }

    @Benchmark
    public Pair<String, Pair<NeuralModel, Progress>> defaultTraining(BenchmarkState state) throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = learningSteps;
        settings.setOptimizer(optimizer);
        settings.infer();
        return neuralTraining(state, settings);
    }

    public static Pair<String, Pair<NeuralModel, Progress>> neuralTraining(BenchmarkState state, Settings settings) throws Exception {
        TrainingBuilder.NeuralLearningBuilder neuralLearningBuilder = new TrainingBuilder.NeuralLearningBuilder(settings);
        Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> trainingPipeline = neuralLearningBuilder.buildPipeline();
        Pair<String, Pair<NeuralModel, Progress>> trainingResults = trainingPipeline.execute(new Pair<>(state.neuralModel, state.samples.stream()));
        return trainingResults;
    }
}
