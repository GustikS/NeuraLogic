package cz.cvut.fel.ida.neuralogic.cli.benchmarks;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.RunnerException;

import java.time.Duration;
import java.util.Collection;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Benchmarking.assertSmallRuntimeDeviation;
import static cz.cvut.fel.ida.utils.generic.Benchmarking.benchmarkSlow;
import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

/**
 * The reference times measured within IntelliJ during the full fastTestSuite run
 */
public class MainStagesMutaBenchmarks {
    private static final Logger LOG = Logger.getLogger(MainStagesMutaBenchmarks.class.getName());

    static String dataset = "relational/molecules/mutagenesis";
    static String template = "-t ./templates/template_gnnW10.txt";
//    static String template = "-t ./templates/template_rings_gnn.txt";


    @TestAnnotations.PreciseBenchmark
    public void benchmarkMutagenesisLoading() throws RunnerException {
        Duration referenceTime = Duration.ofMillis(340);
        double maxDeviation = 7;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".mutagenesis1SampleProcessing", 5, 2);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    @TestAnnotations.PreciseBenchmark
    public void benchmarkMutagenesisGrounding() throws RunnerException {
        Duration referenceTime = Duration.ofMillis(5000);
        double maxDeviation = 7;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".mutagenesisGrounding", 3, 1);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    @TestAnnotations.Slow
    public void benchmarkMutagenesisFullTraining() throws RunnerException {
        Duration referenceTime = Duration.ofSeconds(18);
        double maxDeviation = 0.2;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".mutagenesisFullTraining", 2, 1);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    @Benchmark
    public void mutagenesis1SampleProcessing() throws Exception {
        Settings settings = Settings.forMediumTest();
        String[] args = getDatasetArgs(dataset, template, "-lim 1 -ts 0");
        Pair<Pipeline, ?> main = Main.main(args, settings);
        System.out.println("mutagenesis1SampleProcessing");
    }

    @Benchmark
    public void mutagenesisGrounding() throws Exception {
        Settings settings = Settings.forMediumTest();
        String[] args = getDatasetArgs(dataset, template, "-lim -1 -ts 0");
        Pair<Pipeline, ?> main = Main.main(args, settings);
        System.out.println(main.s);
    }

    @Benchmark
    @TestAnnotations.Medium
    public void mutagenesisParallelGrounding() throws Exception {
        Settings settings = Settings.forMediumTest();
        settings.parallelGrounding = true;
        String[] args = getDatasetArgs(dataset, template, "-lim -1 -ts 0");
        Pair<Pipeline, ?> main = Main.main(args, settings);
        System.out.println(main.s);
    }

    @Benchmark
    public void mutagenesisFullTraining() throws Exception {
        Settings settings = Settings.forMediumTest();
        String[] args = getDatasetArgs(dataset, template, "-lim -1 -ts 100");
        Pair<Pipeline, ?> main = Main.main(args, settings);
        System.out.println(main.s);
    }
}
