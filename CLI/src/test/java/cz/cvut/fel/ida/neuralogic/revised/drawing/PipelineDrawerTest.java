package cz.cvut.fel.ida.neuralogic.revised.drawing;

import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.building.LearningSchemeBuilder;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.debuging.drawing.PipelineDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class PipelineDrawerTest {
    @TestAnnotations.Interactive
    public void mutagen() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        Sources sources = Runner.getSources(getDatasetArgs("relational/molecules/mutagenesis"), settings);

        PipelineDebugger pipelineDebugger = new PipelineDebugger(settings);

        AbstractPipelineBuilder<Sources, ?> builder = LearningSchemeBuilder.getBuilder(sources, settings);
        Pipeline<Sources, ?> sourcesPipeline = builder.buildPipeline();
        pipelineDebugger.debug(sourcesPipeline);
    }
}