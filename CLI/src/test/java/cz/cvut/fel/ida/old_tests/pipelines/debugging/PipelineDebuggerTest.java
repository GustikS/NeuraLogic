package cz.cvut.fel.ida.old_tests.pipelines.debugging;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.building.LearningSchemeBuilder;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.debuging.drawing.PipelineDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import org.junit.Test;

public class PipelineDebuggerTest {

    @Test
    public void family() throws Exception {
        Logging logging = Logging.initLogging();
        String[] args = new String("-path ./resources/datasets/family").split(" ");

        Settings settings = new Settings();

        Sources sources = Runner.getSources(args, settings);
        PipelineDebugger pipelineDebugger = new PipelineDebugger(settings);

        AbstractPipelineBuilder<Sources, ?> builder = LearningSchemeBuilder.getBuilder(sources, settings);
        Pipeline<Sources, ?> sourcesPipeline = builder.buildPipeline();
        pipelineDebugger.debug(sourcesPipeline);
    }

}