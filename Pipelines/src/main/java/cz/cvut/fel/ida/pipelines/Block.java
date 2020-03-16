package cz.cvut.fel.ida.pipelines;

import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.exporting.Exporter;
import cz.cvut.fel.ida.utils.generic.Timing;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public abstract class Block {
    private static final Logger LOG = Logger.getLogger(Block.class.getName());

    public String ID;

    protected Pipeline parent;
    public Exporter exporter;
    public Settings settings;

    public Pipeline getRoot() {
        Pipeline par = parent;
        while (par.parent != null) {
            par = par.parent;
        }
        return par;
    }

    public static Exporter createExporter(String id, Settings settings) {
        if (settings == null){
            return null;
        }
        return Exporter.getFrom(settings.exportDir, id, settings.exportBlocks, settings.blockExporting.name());
    }

    protected <T> void export(T outputReady) {
        if (outputReady instanceof Exportable) {
            if (exporter == null && parent != null) {
                exporter = createExporter(this.ID, settings);
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
