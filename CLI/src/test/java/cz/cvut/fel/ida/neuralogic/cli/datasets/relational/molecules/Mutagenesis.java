package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.utils.WorkflowUtils;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Benchmarking;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.time.Duration;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class Mutagenesis {
    private static final Logger LOG = Logger.getLogger(Mutagenesis.class.getName());

    static String dataset = "relational/molecules/mutagenesis";

    @TestAnnotations.Slow
    public void defaultMutagenPerformance() throws Exception {
        double referenceDispersion = 0.323;
        Duration referenceTime = Duration.ofMinutes(10);

        Settings settings = Settings.forSlowTest();
        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset), settings);
        Benchmarking.assertDispersionAndTime(WorkflowUtils.getDisperionAndTime(results), referenceDispersion, referenceTime);
    }
}
