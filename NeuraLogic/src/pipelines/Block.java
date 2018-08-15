package pipelines;

import settings.Settings;
import utils.Exporter;

import java.util.logging.Logger;

public abstract class Block {
    private static final Logger LOG = Logger.getLogger(Block.class.getName());

    Pipeline parent;
    Exporter exporter;
    Settings settings;

}
