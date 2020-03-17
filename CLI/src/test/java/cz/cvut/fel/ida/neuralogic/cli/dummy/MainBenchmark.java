package cz.cvut.fel.ida.neuralogic.cli.dummy;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.RunnerException;

import java.time.Duration;
import java.util.Collection;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Benchmarking.benchmarkSlow;
import static cz.cvut.fel.ida.utils.generic.Utilities.splitArgs;

public class MainBenchmark {
    private static final Logger LOG = Logger.getLogger(MainBenchmark.class.getName());

    @TestAnnotations.SlowBenchmark
    public void dummyBenchTest() throws RunnerException {
        Duration referenceTime = Duration.ofSeconds(1000);
        double maxDeviation = 0.05;

        Collection<RunResult> runResults = benchmarkSlow(this.getClass().getName());
//        assertSmallDeviation(runResults, referenceTime, maxDeviation);
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({
                "  ",
                ""
        })
        public String argString;

        public String[] args;

        @Setup(Level.Trial)
        public void setUp() {
            args = splitArgs(argString);
        }
    }

    @Benchmark
    public void main(BenchmarkState state) {
        Main.main(state.args);
    }
}
