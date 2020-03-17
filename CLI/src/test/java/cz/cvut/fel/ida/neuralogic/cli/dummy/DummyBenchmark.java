package cz.cvut.fel.ida.neuralogic.cli.dummy;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.RunnerException;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Benchmarking.assertSmallRuntimeDeviation;
import static cz.cvut.fel.ida.utils.generic.Benchmarking.benchmarkFast;

public class DummyBenchmark {
    private static final Logger LOG = Logger.getLogger(DummyBenchmark.class.getName());

    @Test
    public void dummyBenchTest() throws RunnerException {
        Duration referenceTime = Duration.ofNanos(100000);
        double maxDeviation = 0.5;

        Collection<RunResult> runResults = benchmarkFast(getClass().getName() + ".blabla");
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    @Benchmark
    public List blabla(){
        Random rand = new Random();
        List list = new LinkedList<>();
        for (int i = 0; i < 400; i++)
            list.add(rand.nextInt());
        for (int i = 0; i < 400; i++) {
            list.get(i);
        }
        return list;
    }

    @Benchmark
    public List dummyBenchmarkIter2(){
        Random rand = new Random();
        List list = new LinkedList<>();
        for (int i = 0; i < 1000; i++)
            list.add(rand.nextInt());
        for (int i = 0; i < 1000; i++) {
            list.get(i);
        }
        return list;
    }
}
