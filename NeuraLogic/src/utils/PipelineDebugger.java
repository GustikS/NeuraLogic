package utils;

import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.building.End2endTrainigBuilder;
import settings.Settings;
import settings.Sources;
import utils.drawing.Drawer;
import utils.drawing.PipelineDrawer;

import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class PipelineDebugger<S> {
    private static final Logger LOG = Logger.getLogger(PipelineDebugger.class.getName());

    /**
     * i.e. what are we debugging?
     */
    S debuggingInput;

    /**
     * what we debug is at the end of the pipeline...
     */
    protected Pipeline<Sources, Stream<S>> pipeline = null;

    /**
     * we debug mainly by drawing
     */
    public Drawer<S> drawer;

    /**
     * debugging always starts from the very beginning, i.e. Sources
     */
    protected Sources sources;
    protected Settings settings;

    /**
     * we build the pipeline in the most straightforward way using End2endTrainigBuilder
     */
    protected End2endTrainigBuilder end2endTrainigBuilder;

    private PipelineDebugger(Settings settings) {
        //for calling the auxiliary functions
    }

    public PipelineDebugger(String[] args) {
        this(args, new Settings());
    }

    public PipelineDebugger(String[] args, Settings settings) {
        this.sources = Sources.getSources(args, settings);
        this.settings = settings;
        this.end2endTrainigBuilder = new End2endTrainigBuilder(settings, sources);
        this.pipeline = new Pipeline<>("DebuggingPipeline", settings);
    }

    public PipelineDebugger(Sources sources, Settings settings) {
        this.sources = sources;
        this.settings = settings;
        this.end2endTrainigBuilder = new End2endTrainigBuilder(settings, sources);
        this.pipeline = new Pipeline<>("DebuggingPipeline", settings);
    }

    public void executeDebug() {
        pipeline.connectAfter(new Pipe<Stream<S>, Stream<S>>("PeekPipe") {
            @Override
            public Stream<S> apply(Stream<S> stream) {
                return stream.peek(PipelineDebugger.this::debug);
            }
        });
        pipeline.execute(sources);
    }

    public abstract void debug(S obj);

    public void drawPipeline(){
        PipelineDrawer pipelineDrawer = new PipelineDrawer(settings);
        pipelineDrawer.draw(pipeline);
    }

}
