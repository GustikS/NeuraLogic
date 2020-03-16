package pipelines.debuging;

import building.LearningSchemeBuilder;
import org.junit.Test;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.debuging.drawing.PipelineDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import utils.Runner;
import utils.logging.Logging;

import java.util.logging.Level;

public class PipelineDebuggerTest {

    @Test
    public void family() {
        Logging logging = Logging.initLogging(Level.FINEST);
        String[] args = new String("-path ./resources/datasets/family").split(" ");

        Settings settings = new Settings();

        Sources sources = Runner.getSources(args, settings);
        PipelineDebugger pipelineDebugger = new PipelineDebugger(settings);

        AbstractPipelineBuilder<Sources, ?> builder = LearningSchemeBuilder.getBuilder(sources, settings);
        Pipeline<Sources, ?> sourcesPipeline = builder.buildPipeline();
        pipelineDebugger.debug(sourcesPipeline);
    }

}