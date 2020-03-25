package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class MutaMini {
    private static final Logger LOG = Logger.getLogger(MutaMini.class.getName());

    //muta_mini doesnt have a single file template
    static String[] dataset = getDatasetArgs("relational/molecules/muta_mini","-sd  -t ./embeddings,./template.txt");

    @TestAnnotations.Medium
    public void basic() throws Exception {
        Settings settings = Settings.forFastTest();
        Main.main(dataset, settings);
    }
}
