package cz.cvut.fel.ida.utils.generic;

import org.openjdk.jmh.annotations.Benchmark;
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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Benchmarking {
    private static final Logger LOG = Logger.getLogger(Benchmarking.class.getName());

    /**
     * must stat Millis as there is no easy conversion of it later
     */
    private static TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private static TemporalUnit temporalUnit = ChronoUnit.MILLIS;   //these 2 must be the same as there is no direct conversion between them!

    private static DecimalFormat df = new DecimalFormat("0.000");

    @TestAnnotations.SlowBenchmark
    public void genericPreciesBenchmark(){
        Double coeff = TestLogging.baselinePerformanceCoeff;
        LOG.warning("TESTING NOW");
        double baselinePerformanceCoeff = getBaselinePerformanceCoeff();
        assertEquals(coeff,baselinePerformanceCoeff,0.001);
    }

    @TestAnnotations.Fast
    public void testBaseline() {
        double baselinePerformanceCoeff = getBaselinePerformanceCoeff();
        System.out.println(baselinePerformanceCoeff);
    }

    public static double getBaselinePerformanceCoeff() {
        Duration reference = Duration.ofSeconds(1);
        LOG.warning("----- benchmarking baseline performance of this computer for subsequent performance testing (runtime assertions) PLEASE PAUSE OTHER RUNNING PROCESSES -----");

        try {
            Collection<RunResult> runResults = benchmarkFast(Benchmarking.class.getName() + ".baselinePerformance");
            Double meanTime = getMeanTime(runResults);
            Duration realTime = Duration.of(meanTime.longValue(), temporalUnit);

            LOG.warning(realTime + " vs. expected: " + reference);

            double coeff = realTime.toMillis() / (double) reference.toMillis();
            return coeff;
        } catch (RunnerException e) {
            e.printStackTrace();
        }
        return 1.0;
    }

    /**
     * Tuned for my PC to give a performance coefficient of app. 1.0 = 1s
     * Intel i7-5500 Linux (mint 17.3, kernel 4.4.0)
     * (Only IntelliJ and system performance monitoring turned on)
     * @return
     */
    @Benchmark
    public List<Integer> baselinePerformance() {
        int tuning = 45100;
        Random random = new Random(0);
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < tuning; i++) {
            list.add(random.nextInt());
        }

        List<Integer> out = new LinkedList<>();
        for (int i = 0; i < tuning; i++) {
            out.add(list.get(i));
        }
        return out;
    }


    public static void assertDispersionAndTime(Pair<Double, Duration> results, Double dispersion, Duration referenceTime) {
        assertEquals(dispersion, results.r, 0.01);
        LOG.warning("time taken: " + results.s);
        assertEquals(results.s.toMillis() / (double) referenceTime.toMillis(), 1, 0.01);
    }

    public static void assertSmallRuntimeDeviation(Collection<RunResult> runResults, Duration referenceDuration, double maxDeviation) {

        double score = getMeanTime(runResults);

        Duration realDuration = Duration.of(Math.round(score), temporalUnit);

        LOG.finer("real: " + realDuration.toNanos() + " vs exp: " + referenceDuration.toNanos());

        double deviation = Math.abs((double) realDuration.toNanos() / (double) referenceDuration.toNanos() - 1);
        LOG.warning(realDuration + " vs. expected: " + referenceDuration);
        String deviationString = df.format(deviation * 100) + "%";
        String maxDeviationString = df.format(maxDeviation * 100) + "%";
        String errorMessage = "Deviation " + deviationString + " exceeds maximum allowed deviation " + maxDeviationString;
        assertTrue(deviation < maxDeviation, errorMessage);
    }

    public static long getMeanTime(Collection<RunResult> runResults, TimeUnit requested) {
        Double meanTime = getMeanTime(runResults);
        long longValue = meanTime.longValue();
        long convert = requested.convert(longValue, timeUnit);

        return convert;
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
        if (cvar > 0.5) {
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
//                .warmupTime(TimeValue.seconds(3))
                .warmupIterations(2)
                .measurementTime(TimeValue.milliseconds(1))
                .measurementIterations(15)
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
