package cz.cvut.fel.ida.pipelines.debugging;

import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.pipes.generic.SecondFromPairPipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Just assemble the learning samples
 */
public class SampleDebugger extends End2EndDebugger<LogicSample> {
    private static final Logger LOG = Logger.getLogger(SampleDebugger.class.getName());

    public SampleDebugger(Settings settings) {
        super(settings);
    }

    public SampleDebugger(Sources sources, Settings settings) {
        super(sources, settings);
    }

    @Override
    public void debug(LogicSample obj) {
        LOG.info("debugging a learning sample:" + obj.toString());
    }

    /**
     * Just build template, nothing more
     *
     * @return
     */
    @Override
    public Pipeline<Sources, Stream<LogicSample>> buildPipeline() {
        Pipeline<Sources, Pair<Template, Stream<LogicSample>>> sourcesPairPipeline = pipeline.registerStart(end2endTrainigBuilder.buildFromSources(sources, settings));
        pipeline.registerEnd(pipeline.register(sourcesPairPipeline.connectAfter(new SecondFromPairPipe<>())));
        return pipeline;
    }
}