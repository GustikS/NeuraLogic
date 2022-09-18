package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.util.logging.Logger;

public class Activations {
    private static final Logger LOG = Logger.getLogger(Activations.class.getName());

    /**
     * @throws Exception
     */
    @TestAnnotations.Medium
    public void crossum() throws Exception {

        Settings settings = Settings.forFastTest();

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_crosssum.txt"), settings);
    }

    @TestAnnotations.Medium
    public void elementproduct() throws Exception {

        Settings settings = Settings.forFastTest();

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_elementProduct.txt"), settings);
    }

    @TestAnnotations.Medium
    public void product() throws Exception {

        Settings settings = Settings.forFastTest();

        settings.isoValueCompression = true;
        settings.chainPruning = true;

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_product.txt"), settings);
    }

    @TestAnnotations.Medium
    public void max() throws Exception {

        Settings settings = Settings.forFastTest();

        settings.aggNeuronAggregation = Settings.CombinationFcn.MAX;

        settings.isoValueCompression = false;
        settings.chainPruning = true;
//        settings.pruneOnlyIdentities = true;
//        settings.structuralIsoCompression = true;

//        settings.iterationMode = Settings.IterationMode.BFS;
//        settings.iterationMode = Settings.IterationMode.DFS_STACK;
//        settings.iterationMode = Settings.IterationMode.DFS_RECURSIVE;

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_gnn.txt"), settings);
    }

    @TestAnnotations.Medium
    public void attention() throws Exception {

        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = 10;

//        settings.ruleNeuronActivation = Settings.ActivationFcn.RELU;

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_gnn_attention.txt"), settings);
    }
}