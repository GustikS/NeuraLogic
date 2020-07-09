package cz.cvut.fel.ida.pipelines.debugging;

import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.debugging.drawing.TemplateDrawer;
import cz.cvut.fel.ida.pipelines.pipes.generic.FirstFromPairPipe;
import cz.cvut.fel.ida.pipelines.pipes.generic.StreamifyPipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TemplateDebugger extends End2EndDebugger<Template> {
    private static final Logger LOG = Logger.getLogger(TemplateDebugger.class.getName());

    public TemplateDebugger(Settings settings) {
        super(settings);
        if (settings.drawing)
            drawer = new TemplateDrawer(settings);
    }

    public TemplateDebugger(Sources sources, Settings settings) {
        super(sources, settings);
        if (settings.drawing)
            drawer = new TemplateDrawer(settings);

    }

    @Override
    public void debug(Template obj) {
        LOG.info("drawing a template:" + obj.toString());
        if (settings.drawing)
            drawer.draw(obj);
    }

    /**
     * Just build template, nothing more
     *
     * @return
     */
    @Override
    public Pipeline<Sources, Stream<Template>> buildPipeline() {
        Pipeline<Sources, Pair<Template, Stream<LogicSample>>> sourcesPairPipeline = pipeline.registerStart(end2endTrainigBuilder.buildFromSources(sources, settings));
        pipeline.registerEnd(pipeline.register(sourcesPairPipeline.connectAfter(new FirstFromPairPipe<>())).connectAfter(new StreamifyPipe<>()));
        return pipeline;
    }
}
