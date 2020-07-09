package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.kbs;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
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

public class Kinships {
    private static final Logger LOG = Logger.getLogger(Kinships.class.getName());

    static String dataset = "relational/kbs/kinships";

    /**
     * 1 training step = app. 12s
     * @throws RunnerException
     */
    @TestAnnotations.Slow
    public void benchmarkBasic() throws RunnerException {
        Duration referenceTime = Duration.ofSeconds(65);
        double maxDeviation = 0.2;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".basic", 1, 0);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    @Benchmark
    public void basic() throws Exception {
        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        Main.main(getDatasetArgs(dataset), settings);
    }
}