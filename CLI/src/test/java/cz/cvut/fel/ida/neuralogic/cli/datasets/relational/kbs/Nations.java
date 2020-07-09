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

public class Nations {
    private static final Logger LOG = Logger.getLogger(Nations.class.getName());

    static String dataset = "relational/kbs/nations";

    /**
     * 1 training step = app. 1.5s
     * @throws RunnerException
     */
    @TestAnnotations.Slow
    public void benchmarkBasic() throws RunnerException {
        Duration referenceTime = Duration.ofSeconds(35);
        double maxDeviation = 0.2;

        Collection<RunResult> runResults = benchmarkSlow(getClass().getName() + ".basicKBC", 1, 0);
        assertSmallRuntimeDeviation(runResults, referenceTime, maxDeviation);
    }

    @Benchmark
    public void basicKBC() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.resultsRecalculationEpochae = 10;
        settings.maxCumEpochCount = 20;
        Main.main(getDatasetArgs(dataset,"-t ./templates/template.txt"), settings);
    }

    @TestAnnotations.Slow
    public void Hits() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.hitsCorruption = Settings.HitsCorruption.ONE_SAME;
        settings.passResultsCache = true;
        settings.resultsRecalculationEpochae = 10;
        settings.maxCumEpochCount = 20;
        settings.trainValidationPercentage = 0.8;
        Main.main(getDatasetArgs(dataset,"-em kbc -t ./templates/template.txt"), settings);
    }
}