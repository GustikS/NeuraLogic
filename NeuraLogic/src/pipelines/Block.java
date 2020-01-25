package pipelines;

import settings.Settings;
import utils.exporting.Exportable;
import utils.exporting.Exporter;

import java.util.logging.Logger;

public abstract class Block {
    private static final Logger LOG = Logger.getLogger(Block.class.getName());

    public String ID;

    protected Pipeline parent;
    protected Exporter exporter;
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

    @Override
    public String toString() {
        return ID;
    }
}
