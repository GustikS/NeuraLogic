package cz.cvut.fel.ida.neuralogic.cli.benchmarks;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.annotations.Benchmark;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Benchmarking.assertSmallRuntimeDeviation;
import static cz.cvut.fel.ida.utils.generic.Benchmarking.benchmarkFast;

public class BenchmarkingTemplate {
    private static final Logger LOG = Logger.getLogger(BenchmarkingTemplate.class.getName());

    @TestAnnotations.AdHoc
    public void dummyBenchTest() throws RunnerException {
        Duration referenceTime = Duration.ofNanos(100000);
        double maxDeviation = 0.5;

        Collection<RunResult> runResults = benchmarkFast(getClass().getName() + ".blabla");
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);

        Collection<RunResult> runResults2 = benchmarkFast(getClass().getName() + ".foofoo");
        assertSmallRuntimeDeviation(runResults2, referenceTime, maxDeviation);
    }

    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @State(Scope.Thread)
    public static class BenchmarkState {
        List<Integer> list;

        @Setup(Level.Trial)
        public void
        initialize() {
            Random rand = new Random();
            list = new LinkedList<>();
            for (int i = 0; i < 4000; i++)
                list.add(rand.nextInt());
        }
    }

    @Benchmark
    public List blabla(BenchmarkState state) {
        for (int i = 0; i < 400; i++) {
            state.list.get(i);
        }
        return state.list;
    }

    @Benchmark
    public List foofoo(BenchmarkState state) {
        for (int i = 0; i < 1000; i++) {
            state.list.get(i);
        }
        return state.list;
    }
}
