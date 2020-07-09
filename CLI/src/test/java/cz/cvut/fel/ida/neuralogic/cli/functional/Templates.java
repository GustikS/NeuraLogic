package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class Templates {
    private static final Logger LOG = Logger.getLogger(Templates.class.getName());

    @TestAnnotations.Slow
    public void mutaGraphlets() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.resultsRecalculationEpochae = 10;
        settings.maxCumEpochCount = 100;
        Main.main(getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_graphlets.txt"), settings);
    }
}