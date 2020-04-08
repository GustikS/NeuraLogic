package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class Activations {
    private static final Logger LOG = Logger.getLogger(Activations.class.getName());

    static String dataset = "relational/molecules/e_coli";

    @TestAnnotations.Slow
    public void defaultEcoliPerformance() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.appLimitSamples = 200;
        settings.plotProgress = 5;

//        settings.maxCumEpochCount = 10000;

//        settings.initLearningRate = 0.001;

//        settings.isoValueCompression = true;
//        settings.chainPruning = true;

//        settings.initializer = Settings.InitSet.SIMPLE;

//        settings.ruleNeuronActivation = Settings.ActivationFcn.TANH;
//        settings.atomNeuronActivation = Settings.ActivationFcn.TANH;

        settings.errorFunction = Settings.ErrorFcn.SQUARED_DIFF;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./templates/template_gnnW10_activations.txt -ts 3000 -iso -1 -prune 1 -xval 10 -init simple -atomf line -rulef line -aggf sum"), settings);
    }
}
