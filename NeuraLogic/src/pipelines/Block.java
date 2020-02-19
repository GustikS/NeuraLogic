package pipelines;

import settings.Settings;
import utils.Timing;
import utils.exporting.Exportable;
import utils.exporting.Exporter;

import java.util.logging.Logger;

public abstract class Block {
    private static final Logger LOG = Logger.getLogger(Block.class.getName());

    public String ID;

    protected Pipeline parent;
    public Exporter exporter;
    public Settings settings;

    protected <T> void export(T outputReady) {
        if (outputReady instanceof Exportable) {
            if (exporter == null && parent != null) {
                exporter = Exporter.getFrom(this.ID, parent.settings);
            }
            if (exporter != null) {
                ((Exportable) outputReady).export(this.exporter);
            }
        }
    }

    protected <T> void export(T outputReady, Timing timing) {
        PipelineTiming<T> pipelineTiming = new PipelineTiming<>(outputReady, timing);
        export(pipelineTiming);
    }

    private class PipelineTiming<T> implements Exportable {

        T pipelineOutput;
        Timing timing;

        public PipelineTiming(T pipelineOutput, Timing timing) {
            this.pipelineOutput = pipelineOutput;
            this.timing = timing;
        }

        @Override
        public void export(Exporter exporter) {
            if (pipelineOutput instanceof Exportable) {
                exporter.export(this);
            }
        }
    }

    @Override
    public String toString() {
        return ID;
    }
}
