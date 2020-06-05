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
    public void basic() throws Exception {
        Settings settings = Settings.forSlowTest();
        Main.main(getDatasetArgs(dataset,"-t ./templates/template.txt"), settings);
    }
}
