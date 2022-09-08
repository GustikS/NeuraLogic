package cz.cvut.fel.ida.neuralogic.cli.benchmarks;

import cz.cvut.fel.ida.learning.results.DetailedClassificationResults;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GNNDataBenchmarks {
    private static final Logger LOG = Logger.getLogger(GNNDataBenchmarks.class.getName());

    static String[] datasets = new String[]{"BZR", "COX2", "DHFR", "KKI", "MUTAG", "NCI1", "Peking_1", "PROTEINS"};
    static String[] templates = new String[]{"gcn", "gin", "gsage"};


    static String path = "relational/benchmarks/";

    Settings settings = getSettings();

    @NotNull
    public static Settings getSettings() {
        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.UNIFORM;

        settings.trainValidationPercentage = 1.0;
//        settings.plotProgress = 20;

        settings.seed = 0;
        settings.maxCumEpochCount = 100;
        settings.resultsRecalculationEpochae = 20;

        settings.atomNeuronTransformation = Settings.TransformationFcn.TANH;
        settings.ruleNeuronTransformation = Settings.TransformationFcn.TANH;

//        settings.shuffleEachEpoch = true;
        settings.debugSampleOutputs = false;
//        settings.calculateBestThreshold = true;
//        settings.appLimitSamples = 100;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.initLearningRate = 0.01;
        settings.iterationMode = Settings.IterationMode.TOPOLOGIC;

        settings.errorFunction = Settings.ErrorFcn.SOFTENTROPY;
        settings.inferOutputFcns = true;
        settings.squishLastLayer = true;

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = true;
        settings.chainPruning = true;
        settings.isoValueCompression = true;
        return settings;
    }

    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("BZR", "gcn", 0.12881160833662278, Duration.ofSeconds(20)),
                Arguments.of("BZR", "gsage", 0.25023577263724756, Duration.ofSeconds(15)),
                Arguments.of("BZR", "gin", 0.13574530856295447, Duration.ofSeconds(35)),
                Arguments.of("COX2", "gcn", 0.12907729449786204, Duration.ofSeconds(13)),
                Arguments.of("COX2", "gsage", 0.26923131962175195, Duration.ofSeconds(15)),
                Arguments.of("COX2", "gin", 0.15670609978903421, Duration.ofSeconds(45)),
                Arguments.of("DHFR", "gcn", 0.10653140774952852, Duration.ofSeconds(24)),
                Arguments.of("DHFR", "gsage", 0.16375597617824778, Duration.ofSeconds(29)),
                Arguments.of("DHFR", "gin", 0.08181481006368074, Duration.ofSeconds(80)),
                Arguments.of("MUTAG", "gcn", 0.18674067693767255, Duration.ofSeconds(3)),
                Arguments.of("MUTAG", "gsage", 0.3784652456473076, Duration.ofSeconds(2)),
                Arguments.of("MUTAG", "gin", 0.30992535271839594, Duration.ofSeconds(10)),
                Arguments.of("NCI1", "gcn", 0.059432692575192625, Duration.ofSeconds(131)),
                Arguments.of("NCI1", "gsage", 0.08842048486438836, Duration.ofSeconds(100)),
//                Arguments.of("NCI1", "gin", 0.07733469994560199, Duration.ofMinutes(14)),
                Arguments.of("Peking_1", "gcn", 0.9999771533263031, Duration.ofSeconds(8)),
                Arguments.of("Peking_1", "gsage", 0.9999918836142342, Duration.ofSeconds(9)),
                Arguments.of("Peking_1", "gin", 0.974292164071579, Duration.ofSeconds(17)),
                Arguments.of("PROTEINS", "gcn", 0.0, Duration.ofSeconds(28)),
                Arguments.of("PROTEINS", "gsage", 0.13508886718792362, Duration.ofSeconds(35)),
                Arguments.of("PROTEINS", "gin", 0.0895582739903435, Duration.ofSeconds(157))
        );
    }

    @TestAnnotations.Parameterized
    @MethodSource("data")
    @Tag("Slow")
    public void test(String dataset, String template, double dispersion, Duration referenceTime) throws Exception {

        Pair<Pipeline, ?> main = Main.main(getDatasetArgs(path + dataset, "-t ./template_" + template), settings);
        DetailedClassificationResults results = (DetailedClassificationResults) main.s;

        assertEquals(dispersion, results.dispersion, 0.000001);
        assertEquals(referenceTime.toMillis(), main.r.timing.getTimeTaken().toMillis(), referenceTime.toMillis() * 0.3);
    }


}