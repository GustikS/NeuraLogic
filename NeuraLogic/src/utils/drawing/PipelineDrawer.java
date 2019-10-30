package utils.drawing;

import pipelines.Pipeline;
import settings.Settings;

import java.util.logging.Logger;

public class PipelineDrawer extends Drawer<Pipeline> {
    private static final Logger LOG = Logger.getLogger(PipelineDrawer.class.getName());

    public PipelineDrawer(Settings settings) {
        super(settings);
    }

    @Override
    public void loadGraph(Pipeline obj) {

    }


    //todo use DOT file format and graphviz called externally through stream of bytes
    //alternatively you might use JUNG for more interaction later
}
