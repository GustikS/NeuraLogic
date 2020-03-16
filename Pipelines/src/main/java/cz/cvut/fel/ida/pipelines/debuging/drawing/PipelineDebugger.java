package cz.cvut.fel.ida.pipelines.debuging.drawing;

import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class PipelineDebugger {
    private static final Logger LOG = Logger.getLogger(PipelineDebugger.class.getName());
    private final Settings setting;

    PipelineDrawer pipelineDrawer;

    public PipelineDebugger(Settings settings) {
        this.setting = settings;
        pipelineDrawer = new PipelineDrawer<>(settings);
    }

    public void debug(Pipeline pipeline) {
        //todo more debugging/checking
        pipelineDrawer.draw(pipeline);
    }
}
