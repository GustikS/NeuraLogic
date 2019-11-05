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

public abstract class Debugger<S> extends AbstractPipelineBuilder<Sources, Stream<S>> {
    private static final Logger LOG = Logger.getLogger(Debugger.class.getName());

    /**
     * i.e. what object are we debugging? (Template, GroundSample, NeuralSample,.. ?)
     */
    S debuggingInput;

    /**
     * what we debug is at the end of the pipeline (...the pipeline can be built or obtained)
     */
    protected Pipeline<Sources, Stream<S>> pipeline = null;

    /**
     * we debug mainly by drawing
     */
    public Drawer<S> drawer;

    /**
     * debugging pipeline always starts from the very beginning, i.e. Sources
     */
    protected Sources sources;

    /**
     * we build the pipeline in the most straightforward way using End2endTrainigBuilder
     */
    protected End2endTrainigBuilder end2endTrainigBuilder;

    public boolean intermediateDebug;

    public Debugger(Settings settings) {
        super(settings);
    }

    public Debugger(String[] args) {
        this(args, new Settings());
    }

    public Debugger(String[] args, Settings settings) {
        this(Sources.getSources(args, settings), settings);
    }

    public Debugger(Sources sources, Settings settings) {
        super(settings);
        this.sources = sources;
        this.end2endTrainigBuilder = new End2endTrainigBuilder(settings, sources);
        this.pipeline = new Pipeline<Sources, Stream<S>>(this.getClass().getSimpleName() + "Pipeline", this);
        this.settings.root = pipeline;
        this.intermediateDebug = settings.intermediateDebug;
    }

    public void executeDebug() {
        pipeline = buildPipeline();
        if (settings.debugPipeline){
            drawPipeline();
        }
        addDebugTerminal(pipeline);
        pipeline.execute(sources);
    }

    public void addDebugElement(Pipeline<?, S> pipeline) {
        pipeline.registerEnd(pipeline.terminal.connectAfter(new Pipe<S, S>("PeekPipe") {
            @Override
            public S apply(S s) {
                debug(s);
                return s;
            }
        }));
    }

    public void addDebugStream(Pipeline<?, Stream<S>> pipeline) {
        pipeline.registerEnd(pipeline.terminal.connectAfter(new Pipe<Stream<S>, Stream<S>>("PeekPipe") {
            @Override
            public Stream<S> apply(Stream<S> stream) {
                return stream.peek(Debugger.this::debug);
            }
        }));
    }


    public void addDebugTerminal(Pipeline<?, Stream<S>> pipeline) {
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
    }

    public void drawPipeline() {
        PipelineDrawer<Sources, Stream<S>> pipelineDrawer = new PipelineDrawer<>(settings);
        pipelineDrawer.draw(pipeline);
    }

    public Pipeline<Sources, Stream<S>> getPipeline() {
        return pipeline;
    }

    public abstract void debug(S obj);

}
