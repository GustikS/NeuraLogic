package utils.drawing;

import grounding.GroundingSample;
import settings.Settings;

import java.util.logging.Logger;

public class GroundingDrawer extends Drawer<GroundingSample> {
    private static final Logger LOG = Logger.getLogger(GroundingDrawer.class.getName());

    public GroundingDrawer(Settings settings) {
        super(settings);
    }

    @Override
    public void loadGraph(GroundingSample obj) {

    }

}
