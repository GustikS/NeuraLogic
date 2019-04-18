package constructs.template.transforming;

import constructs.example.QueryAtom;
import constructs.template.Template;
import settings.Settings;

import java.util.logging.Logger;

/**
 * Created by gusta on 14.3.17.
 */
public class LinearChainReducer implements TemplateReducing {
    private static final Logger LOG = Logger.getLogger(LinearChainReducer.class.getName());
    public LinearChainReducer(Settings settings) {

    }

    @Override
    public Template reduce(Template itemplate) {
        LOG.warning("LinearChainReducer for Template not implemented yet");
        return itemplate;
    }

    @Override
    public <T extends Template> T reduce(T itemplate, QueryAtom queryAtom) {
        LOG.warning("LinearChainReducer for Template not implemented yet");
        return itemplate;
    }
}
