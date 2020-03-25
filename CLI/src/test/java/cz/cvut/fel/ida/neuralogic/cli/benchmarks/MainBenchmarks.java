package cz.cvut.fel.ida.neuralogic.cli.benchmarks;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.junit.jupiter.api.Disabled;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.RunnerException;

import java.time.Duration;
import java.util.Collection;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Benchmarking.assertSmallRuntimeDeviation;
import static cz.cvut.fel.ida.utils.generic.Benchmarking.benchmarkSlow;
import static cz.cvut.fel.ida.utils.generic.Utilities.splitArgs;

public class MainBenchmarks {
    private static final Logger LOG = Logger.getLogger(MainBenchmarks.class.getName());

    @Benchmark
    @TestAnnotations.Slow
    public void mutagenesis1SampleLoading() throws Exception {
        String resourcePath = Utilities.getResourcePath("relational/molecules/mutagenesis");
        String args = "-lim 1 -ts 0 -sd " + resourcePath;
        Main.mainExc(splitArgs(args));
    }

    @TestAnnotations.Slow
    public void mutagenesisGrounding() throws Exception {
        String resourcePath = Utilities.getResourcePath("relational/molecules/mutagenesis");
        String args = "-lim -1 -ts 0 -sd " + resourcePath;
        Main.mainExc(splitArgs(args));
    }

    @TestAnnotations.Slow
    @Disabled
    public void mutagenesisFullTraining() throws Exception {
        String resourcePath = Utilities.getResourcePath("relational/molecules/mutagenesis");
        String args = "-lim -1 -ts 100 -sd " + resourcePath;
        Main.mainExc(splitArgs(args));
    }

    @TestAnnotations.SlowBenchmark
    public void benchmarkMutagenesisLoading() throws RunnerException {
        Duration referenceTime = Duration.ofSeconds(4);
        double maxDeviation = 1.5;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".mutagenesis1SampleLoading", 5, 1);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

}
