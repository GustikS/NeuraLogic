package cz.cvut.fel.ida.logic.constructs.template.transforming;

import cz.cvut.fel.ida.logic.constructs.example.QueryAtom;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

/**
 * Created by gusta on 14.3.17.
 */
public class TemplateChainReducer implements TemplateReducing {
    private static final Logger LOG = Logger.getLogger(TemplateChainReducer.class.getName());
    public TemplateChainReducer(Settings settings) {

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
