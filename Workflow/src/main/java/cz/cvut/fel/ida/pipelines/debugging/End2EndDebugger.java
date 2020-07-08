package cz.cvut.fel.ida.pipelines.debugging;

import cz.cvut.fel.ida.drawing.Drawer;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.building.End2endTrainigBuilder;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.debuging.drawing.PipelineDrawer;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.exporting.Exporter;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class End2EndDebugger<S extends Exportable> extends AbstractPipelineBuilder<Sources, Stream<S>> {
    private static final Logger LOG = Logger.getLogger(End2EndDebugger.class.getName());

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


    protected boolean intermediateDebug;
    protected boolean exporting;
    protected boolean drawing;

    protected Exporter exporter;

    public End2EndDebugger(Settings settings) {
        super(settings);
        this.drawing = settings.drawing;
        this.exporting = settings.debugExporting;
        this.intermediateDebug = settings.intermediateDebug;
        if (exporting) {
            startExport();
        }
    }

    public End2EndDebugger(Sources sources, Settings settings) {
        super(settings);
        this.sources = sources;
        this.end2endTrainigBuilder = new End2endTrainigBuilder(settings, sources);
        this.pipeline = new Pipeline<Sources, Stream<S>>(this.getClass().getSimpleName() + "Pipeline", this);
//        this.settings.root = pipeline;
        this.intermediateDebug = settings.intermediateDebug;
        this.exporting = settings.debugExporting;
        if (exporting) {
            startExport();
        }
        this.drawing = settings.drawing;
    }

    public Pair<String, Stream<S>> executeDebug() throws Exception {
        pipeline = buildPipeline();
        if (settings.debugPipeline) {
            drawPipeline();
        }
        addDebugTerminal(pipeline);
        return pipeline.execute(sources);
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
                return stream.peek(s -> {
                    if (exporting)
                        exportSample(s);
                    debug(s);
                });
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
                        if (exporting)
                            exportSample(s);
                        debug(s);
                    }
                });
                endExport();
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

    protected void exportSample(S sample) {
        exporter.export(sample);
        if (settings.exportType == Settings.ExportFileType.JSON)
            exporter.delimitNext();
    }

    protected void startExport() {
        exporter = Exporter.getExporter(Paths.get(settings.exportDir, "debug").toString(), this.getClass().getSimpleName(), settings.exportType.name());
        if (settings.exportType == Settings.ExportFileType.JSON)
            exporter.delimitStart();
    }

    protected void endExport() {
        if (settings.exportType == Settings.ExportFileType.JSON) {
            exporter.delimitEnd();
        }
        exporter.finish();
    }

}
