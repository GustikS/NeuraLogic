package utils.drawing;

import org.junit.Test;
import pipelines.Pipeline;
import pipelines.building.AbstractPipelineBuilder;
import pipelines.debug.PipelineDebugger;
import settings.Settings;
import settings.Sources;
import utils.logging.Logging;

import java.util.logging.Level;

import static org.junit.Assert.*;

public class PipelineDrawerTest {
    @Test
    public void mutagen() {
        Logging logging = Logging.initLogging(Level.FINEST);
        String[] args = "-path ./resources/datasets/mutagenesis".split(" ");

        Settings settings = new Settings();

        Sources sources = Sources.getSources(args, settings);
        PipelineDebugger pipelineDebugger = new PipelineDebugger(settings);

        AbstractPipelineBuilder<Sources, ?> builder = AbstractPipelineBuilder.getBuilder(sources, settings);
        Pipeline<Sources, ?> sourcesPipeline = builder.buildPipeline();
        pipelineDebugger.debug(sourcesPipeline);
    }
}