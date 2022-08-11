package cz.cvut.fel.ida.neuralogic.cli.datasets;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.learning.results.DetailedClassificationResults;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Debug {
    private static final Logger LOG = Logger.getLogger(Debug.class.getName());


    @TestAnnotations.Fast
    public void xor() throws Exception {
        String dataset = "neural/xor/relational_debug2";
        Settings settings = Settings.forSlowTest();
        settings.seed = 4;
//        settings.debugPipeline = true;
//        settings.drawing = true;
        settings.squishLastLayer = false;
        settings.inferOutputFcns = false;
//        settings.plotProgress = 1;
        settings.maxCumEpochCount = 100;
//        settings.isoValueCompression = true;
        settings.losslessIsoCompression = true;
        settings.initLearningRate = 0.01;
        Main.main(getDatasetArgs(dataset,"-t ./template.txt"), settings);
    }

    @TestAnnotations.Fast
    public void minAgg() throws Exception {
        String dataset = "debug/min";
        Settings settings = Settings.forSlowTest();
        settings.squishLastLayer = false;   //turn off both of these to avoid applying sigmoid on top
        settings.inferOutputFcns = false;

        settings.maxCumEpochCount = 1;
        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./template.txt"), settings);
        DetailedClassificationResults classificationResults = (DetailedClassificationResults) results.s;
        ScalarValue output = (ScalarValue) classificationResults.evaluations.get(0).getOutput();
        assertEquals(-10, output.value);
    }

    @TestAnnotations.Interactive
    public void pruning() throws Exception {
        String dataset = "debug/pruning";
        Settings settings = Settings.forInteractiveTest();

        settings.isoValueCompression = false;
        settings.chainPruning = true;
        settings.pruneOnlyIdentities = true;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-debug all"), settings);
    }


    @TestAnnotations.Interactive
    public void activations() throws Exception {
        String dataset = "debug/activations";
        Settings settings = Settings.forInteractiveTest();

        settings.isoValueCompression = false;
        settings.chainPruning = true;
        settings.pruneOnlyIdentities = true;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-debug all"), settings);
    }

    @TestAnnotations.Fast
    public void negation() throws Exception {
        String dataset = "debug/negation";
        Settings settings = Settings.forSlowTest();
        settings.squishLastLayer = false;   //turn off both of these to avoid applying sigmoid on top
        settings.inferOutputFcns = false;
//        settings.isoValueCompression = false;
//        settings.chainPruning = false;
        settings.pruneOnlyIdentities = true;

        settings.maxCumEpochCount = 1;
        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./template.txt"), settings);
        DetailedClassificationResults classificationResults = (DetailedClassificationResults) results.s;
        ScalarValue output = (ScalarValue) classificationResults.evaluations.get(0).getOutput();
        assertEquals(0.9, output.value, 0.00000001);
    }

    @TestAnnotations.Interactive
    public void drawing() throws Exception {
        String dataset = "debug/drawing";
        Settings settings = Settings.forInteractiveTest();

        settings.isoValueCompression = false;
        settings.chainPruning = false;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-debug all"), settings);
    }

    @TestAnnotations.Interactive
    public void duplicits() throws Exception {
        String dataset = "debug/duplicits";
        Settings settings = Settings.forInteractiveTest();

        settings.isoValueCompression = false;
        settings.chainPruning = false;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-debug all"), settings);
    }

    @TestAnnotations.Interactive
    public void factValues() throws Exception {
        String dataset = "debug/facts";
        Settings settings = Settings.forInteractiveTest();

        settings.isoValueCompression = false;
        settings.chainPruning = false;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-debug all"), settings);
    }

    @TestAnnotations.Fast
    public void transpose() throws Exception {
        String dataset = "debug/transpose";
        Settings settings = Settings.forSlowTest();
        settings.softNegation = Settings.TransformationFcn.TRANSP;

        settings.isoValueCompression = false;
        settings.chainPruning = false;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, ""), settings);
        System.out.println(results);
        assertNotNull(results);
    }

    @TestAnnotations.Fast
    public void transpose2() throws Exception {
        String dataset = "debug/transpose2";
        Settings settings = Settings.forSlowTest();
        settings.softNegation = Settings.TransformationFcn.TRANSP;

        settings.isoValueCompression = false;
        settings.chainPruning = false;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, ""), settings);
        System.out.println(results);
        assertNotNull(results);
    }
}