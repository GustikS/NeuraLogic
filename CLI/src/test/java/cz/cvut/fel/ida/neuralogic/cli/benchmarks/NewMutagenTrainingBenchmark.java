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


/**
 * The reference times measured within IntelliJ during the full fastTestSuite run
 */
public class NewMutagenTrainingBenchmark {
    private static final Logger LOG = Logger.getLogger(NewMutagenTrainingBenchmark.class.getName());

    @TestAnnotations.PreciseBenchmark
    public void testTrainingFastEnough() throws RunnerException {
        Duration referenceTime = Duration.ofSeconds(11);
        double maxDeviation = 7;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".defaultTraining", 3, 1);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    @TestAnnotations.AdHoc
    public void debugVersion() throws Exception {
        GroundingState defaultBenchmarkState = new GroundingState();
        defaultBenchmarkState.initialize();
        defaultTraining(defaultBenchmarkState);
    }


    /**
     * Prepare the grounded and neuralized networks here for all the tests!
     */
    @State(Scope.Benchmark)
    public static class GroundingState {
        Sources sources;

        NeuralModel neuralModel;
        List<NeuralSample> samples;

        Settings settings = Settings.forMediumTest();
        String[] arguments = getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_gnnW10.txt");

        void setup(String[] arguments, Settings settings) {
            this.arguments = arguments;
            this.settings = settings;
        }

        @Setup(Level.Trial)
        public void initialize() throws Exception {
            Logging.initLogging(settings);
            settings.maxCumEpochCount = 100;
            LOG.warning("Initializing state!");
            System.out.println(arguments);
            System.out.println(settings);
            sources = Runner.getSources(arguments, settings);

            End2endTrainigBuilder.End2endNNBuilder end2endNNBuilder = new End2endTrainigBuilder(settings, sources).new End2endNNBuilder();
            Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> end2EndNNbuilding = end2endNNBuilder.buildPipeline();
            Pair<String, Pair<NeuralModel, Stream<NeuralSample>>> stringPairPair = end2EndNNbuilding.execute(sources);

            samples = stringPairPair.s.s.collect(Collectors.toList());
            neuralModel = stringPairPair.s.r;
        }
    }

    @Benchmark
    public Pair<String, Pair<NeuralModel, Progress>> defaultTraining(GroundingState state) throws Exception {
        return neuralTraining(state, state.settings);
    }

    public static Pair<String, Pair<NeuralModel, Progress>> neuralTraining(GroundingState state, Settings settings) throws Exception {
        TrainingBuilder.NeuralLearningBuilder neuralLearningBuilder = new TrainingBuilder.NeuralLearningBuilder(settings);
        Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> trainingPipeline = neuralLearningBuilder.buildPipeline();
        Pair<String, Pair<NeuralModel, Progress>> trainingResults = trainingPipeline.execute(new Pair<>(state.neuralModel, state.samples.stream()));
        return trainingResults;
    }
}
