package constructs.template.transforming;

import constructs.template.Template;

import java.util.logging.Logger;

/**
 * Created by gusta on 14.3.17.
 */
public class SimpleTemplateMerger implements TemplateMerging {
    private static final Logger LOG = Logger.getLogger(SimpleTemplateMerger.class.getName());
    @Override
    public Template merge(Template a, Template b) {
        LOG.warning("LinearChainReducer not implemented yet");
        return null;
    }
}
