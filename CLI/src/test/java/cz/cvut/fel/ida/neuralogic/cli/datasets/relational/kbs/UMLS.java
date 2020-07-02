package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.kbs;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class UMLS {
    private static final Logger LOG = Logger.getLogger(UMLS.class.getName());

    static String dataset = "relational/kbs/umls";

    @TestAnnotations.Slow
    public void basicKBC() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.hitsCorruption = Settings.HitsCorruption.ONE_DIFF;
        settings.hitsPreservation = Settings.HitsPreservation.MIDDLE_STAYS;
        settings.passResultsCache = true;
        settings.resultsRecalculationEpochae = 10;
        settings.maxCumEpochCount = 100;
        settings.trainValidationPercentage = 0.8;
        Main.main(getDatasetArgs(dataset,"-em kbc -t ./templates/template.txt"), settings);
    }

    @TestAnnotations.Slow
    public void distmult() throws Exception {
        Settings settings = Settings.forSlowTest();
        Main.main(getDatasetArgs(dataset,"-t ./templates/distmult.txt"), settings);
    }

    @TestAnnotations.Slow
    public void concat() throws Exception {
        Settings settings = Settings.forSlowTest();
        Main.main(getDatasetArgs(dataset,"-t ./templates/concat.txt"), settings);
    }

    @TestAnnotations.Slow
    public void softmax() throws Exception {
        Settings settings = Settings.forSlowTest();
        Main.main(getDatasetArgs(dataset,"-t ./templates/softmax.txt"), settings);
    }

    @TestAnnotations.Slow
    public void sparsemax() throws Exception {
        Settings settings = Settings.forSlowTest();
        Main.main(getDatasetArgs(dataset,"-t ./templates/sparsemax.txt"), settings);
    }
}
