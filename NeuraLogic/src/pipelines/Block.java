package pipelines;

import settings.Settings;
import utils.Exporter;

import java.util.logging.Logger;

public abstract class Block {
    private static final Logger LOG = Logger.getLogger(Block.class.getName());

    public String ID;

    protected Pipeline parent;
    protected Exporter exporter;
    protected Settings settings;

    @Override
    public String toString() {
        return ID;
    }
}
