package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class DecayingRate {
    private static final Logger LOG = Logger.getLogger(DecayingRate.class.getName());
    static String dataset = "relational/molecules/mutagenesis";

    @TestAnnotations.Slow
    public void decayingRate() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);

        settings.initLearningRate = 0.00001;

        settings.islearnRateDecay = false;
        settings.decaySet = Settings.DecaySet.GEOMETRIC;
        settings.decaySteps = 10;
        settings.learnRateDecay = 0.9;

        settings.maxCumEpochCount = 100;

        settings.isoValueCompression = true;
        settings.chainPruning = true;


        settings.ruleNeuronActivation = Settings.ActivationFcn.TANH;
        settings.atomNeuronActivation = Settings.ActivationFcn.SIGMOID;


        settings.plotProgress = 15;
        settings.trainValidationPercentage = 0.9;

//        settings.appLimitSamples = 200;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset), settings);
    }
}
