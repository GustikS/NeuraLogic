package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.kbs;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class Nations {
    private static final Logger LOG = Logger.getLogger(Nations.class.getName());

    static String dataset = "relational/kbs/nations";

    @TestAnnotations.Slow
    public void basicKBC() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.resultsRecalculationEpochae = 10;
        settings.maxCumEpochCount = 100;
        Main.main(getDatasetArgs(dataset,"-t ./templates/template_gnnlike.txt"), settings);
    }

}
