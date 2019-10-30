package utils;

import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.building.AbstractPipelineBuilder;
import pipelines.building.End2endTrainigBuilder;
import settings.Settings;
import settings.Sources;
import utils.drawing.Drawer;
import utils.drawing.PipelineDrawer;

import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class PipelineDebugger<S> extends AbstractPipelineBuilder<Sources, Stream<S>> {
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

    /**
     * we build the pipeline in the most straightforward way using End2endTrainigBuilder
     */
    protected End2endTrainigBuilder end2endTrainigBuilder;

    public boolean intermediateDebug;

    public PipelineDebugger(String[] args) {
        this(args, new Settings());
    }

    public PipelineDebugger(String[] args, Settings settings) {
        this(Sources.getSources(args, settings), settings);
    }

    public PipelineDebugger(Sources sources, Settings settings) {
        super(settings);
        this.sources = sources;
        this.end2endTrainigBuilder = new End2endTrainigBuilder(settings, sources);
        this.pipeline = new Pipeline<Sources, Stream<S>>(this.getClass().getSimpleName() + "Pipeline", this);
        this.settings.root = pipeline;
        this.intermediateDebug = settings.intermediateDebug;
    }

    public void addDebug(Pipeline<?, Stream<S>> pipeline) {
        pipeline.registerEnd(pipeline.terminal.connectAfter(new Pipe<Stream<S>, Stream<S>>("PeekPipe") {
            @Override
            public Stream<S> apply(Stream<S> stream) {
                return stream.peek(PipelineDebugger.this::debug);
            }
        }));
    }

    public void executeDebug() {
        pipeline = buildPipeline();
        pipeline.registerEnd(pipeline.terminal.connectAfter(new Pipe<Stream<S>, Stream<S>>("StreamTerminationPipe") {
            @Override
            public Stream<S> apply(Stream<S> stream) {
                stream.forEach(new Consumer<S>() {
                    @Override
                    public void accept(S s) {
                        debug(s);
                    }
                });
                return stream;  //obviously the returned stream is void now, should not be used (but we need to keep the pipe I-O interface)
            }
        }));
        pipeline.execute(sources);
    }

    public abstract void debug(S obj);

    public void drawPipeline() {
        PipelineDrawer pipelineDrawer = new PipelineDrawer(settings);
        pipelineDrawer.draw(pipeline);
    }

    public Pipeline<Sources, Stream<S>> getPipeline() {
        return pipeline;
    }

}
