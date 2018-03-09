package constructs.template.metadata;

import java.util.logging.Logger;

/**
 * Created by gusta on 1.3.18.
 */
public class TemplateMetadata extends Metadata {
    private static final Logger LOG = Logger.getLogger(TemplateMetadata.class.getName());

    @Override
    public boolean addValidateMetadatum(String parameter, Object Value) {
        return false;
    }
}
