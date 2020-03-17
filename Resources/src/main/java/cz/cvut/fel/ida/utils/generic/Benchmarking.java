package cz.cvut.fel.ida.utils.generic;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.util.Statistics;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Benchmarking {
    private static final Logger LOG = Logger.getLogger(Benchmarking.class.getName());

    /**
     * must stat Millis as there is no easy conversion of it later
     */
    private static TimeUnit timeUnit = TimeUnit.NANOSECONDS;

    private static DecimalFormat df = new DecimalFormat("0.000");

    public static void assertSmallRuntimeDeviation(Collection<RunResult> runResults, Duration referenceScore, double maxDeviation) {
        double referenceScoreMillis = referenceScore.toNanos();

        double score = getMeanTime(runResults);
        double deviation = Math.abs(score / referenceScoreMillis - 1);
        LOG.info(score + " vs. expected: " + referenceScoreMillis);
        String deviationString = df.format(deviation * 100) + "%";
        String maxDeviationString = df.format(maxDeviation * 100) + "%";
        String errorMessage = "Deviation " + deviationString + " exceeds maximum allowed deviation " + maxDeviationString;
        assertTrue(deviation < maxDeviation, errorMessage);
    }

    public static Double getMeanTime(Collection<RunResult> runResults) {
        RunResult runResult = null;
        if (runResults.size() > 1) {
            LOG.warning("There were multiple benchmarsk run together, dunno which one to test!");
            return null;
        } else if (runResults == null || runResults.isEmpty()) {
            LOG.warning("No benchmark results wre collected");
            return null;
        } else {
            runResult = runResults.iterator().next();
        }
        Result primaryResult = runResult.getAggregatedResult().getPrimaryResult();
        Statistics statistics = primaryResult.getStatistics();
        double cvar = statistics.getStandardDeviation() / statistics.getMean();
        if (cvar > 0.2) {
            LOG.warning("Benchmark measured performance vary too much across individual runs! cvar=" + df.format(cvar * 100) + "%");
        }
        String scoreUnit = primaryResult.getScoreUnit();

        return statistics.getMean();
    }

    public static Collection<RunResult> benchmarkFast(String methodPath) throws RunnerException {

        Options opt = new OptionsBuilder()
                // Specify which benchmarks to run.
                // You can be more specific if you'd like to run only one benchmark per test.
                .include(methodPath)
                // Set the following options as needed
                .mode(Mode.AverageTime)
                .timeUnit(timeUnit)
                .warmupTime(TimeValue.seconds(3))
                .warmupIterations(10)
                .measurementTime(TimeValue.milliseconds(1))
                .measurementIterations(20)
                .threads(4)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                //.jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
//                .addProfiler(WinPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
                .build();

        Collection<RunResult> runResults = new Runner(opt).run();
        return runResults;
    }

    public static Collection<RunResult> benchmarkSlow(String methodPath) throws RunnerException {
        return benchmarkSlow(methodPath, 1, 0);
    }

    public static Collection<RunResult> benchmarkSlow(String methodPath, int iterations, int warmupIterations) throws RunnerException {

        Options opt = new OptionsBuilder()
                // Specify which benchmarks to run.
                // You can be more specific if you'd like to run only one benchmark per test.
                .include(methodPath)
                // Set the following options as needed
                .mode(Mode.SingleShotTime)
                .timeUnit(timeUnit)
//                .warmupTime(TimeValue.milliseconds(1))
                .warmupIterations(warmupIterations)
//                .measurementTime(TimeValue.seconds(5))
                .measurementIterations(iterations)
                .threads(1)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                //.jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
//                .addProfiler(WinPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
                .build();

        Collection<RunResult> runResults = new Runner(opt).run();
        return runResults;
    }
}
