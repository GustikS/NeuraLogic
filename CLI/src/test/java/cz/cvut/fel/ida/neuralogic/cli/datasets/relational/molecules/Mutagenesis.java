package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class Mutagenesis {
    private static final Logger LOG = Logger.getLogger(Mutagenesis.class.getName());

    static String dataset = "relational/molecules/mutagenesis";

    @TestAnnotations.Medium
    public void basic() throws Exception {
        Settings settings = Settings.forFastTest();
        Main.main(getDatasetArgs(dataset), settings);
    }
}
