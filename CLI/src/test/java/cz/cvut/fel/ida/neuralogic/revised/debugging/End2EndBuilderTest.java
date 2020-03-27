package cz.cvut.fel.ida.neuralogic.revised.debugging;

import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.building.End2endTrainigBuilder;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.setup.Settings.ExportFileType.JAVA;
import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class End2EndBuilderTest {

    private static final Logger LOG = Logger.getLogger(End2EndBuilderTest.class.getName());

    @TestAnnotations.Slow
    public void simple() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.intermediateDebug = false;
        settings.exportType = JAVA;
        settings.debugExporting = true;


        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);
        End2endTrainigBuilder end2EndTrainigBuilder = new End2endTrainigBuilder(settings, sources);
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> pipeline = end2EndTrainigBuilder.buildPipeline();
        Pair<String, Pair<Pair<Template, NeuralModel>, Progress>> result = pipeline.execute(sources);
        LOG.fine(result.exportToJson());
        assertNotNull(result.s.r.s.allWeights);
    }

}