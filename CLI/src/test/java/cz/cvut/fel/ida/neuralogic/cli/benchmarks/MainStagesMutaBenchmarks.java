package cz.cvut.fel.ida.neuralogic.cli.benchmarks;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.RunnerException;

import java.time.Duration;
import java.util.Collection;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Benchmarking.assertSmallRuntimeDeviation;
import static cz.cvut.fel.ida.utils.generic.Benchmarking.benchmarkSlow;
import static cz.cvut.fel.ida.utils.generic.Utilities.splitArgs;

public class MainStagesMutaBenchmarks {
    private static final Logger LOG = Logger.getLogger(MainStagesMutaBenchmarks.class.getName());

    @TestAnnotations.Slow
    public void benchmarkMutagenesisLoading() throws RunnerException {
        Duration referenceTime = Duration.ofMillis(500);
        double maxDeviation = 0.5;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".mutagenesis1SampleProcessing", 5, 2);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    @TestAnnotations.Slow
    public void benchmarkMutagenesisGrounding() throws RunnerException {
        Duration referenceTime = Duration.ofSeconds(12);
        double maxDeviation = 0.2;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".mutagenesisGrounding", 3, 1);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    @TestAnnotations.Slow
    public void benchmarkMutagenesisFullTraining() throws RunnerException {
        Duration referenceTime = Duration.ofSeconds(65);
        double maxDeviation = 0.2;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".mutagenesisFullTraining", 2, 1);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    @Benchmark
    public void mutagenesis1SampleProcessing() throws Exception {
        Settings settings = new Settings();
        String resourcePath = Utilities.getResourcePath("relational/molecules/mutagenesis");
        String args = "-lim 1 -ts 0 -sd " + resourcePath;
        Pair<Pipeline, ?> main = Main.main(splitArgs(args), settings);
        System.out.println(main.s);
    }

    @Benchmark
    public void mutagenesisGrounding() throws Exception {
        Settings settings = new Settings();
        String resourcePath = Utilities.getResourcePath("relational/molecules/mutagenesis");
        String args = "-lim -1 -ts 0 -sd " + resourcePath;
        Main.main(splitArgs(args), settings);
    }

    @Benchmark
    public void mutagenesisFullTraining() throws Exception {
        Settings settings = new Settings();
        String resourcePath = Utilities.getResourcePath("relational/molecules/mutagenesis");
        String args = "-lim -1 -ts 100 -sd " + resourcePath;
        Main.main(splitArgs(args), settings);
    }
}