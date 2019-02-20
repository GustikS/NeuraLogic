package constructs.template.metadata;

import constructs.template.Template;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 1.3.18.
 */
public class TemplateMetadata extends Metadata<Template> {
    private static final Logger LOG = Logger.getLogger(TemplateMetadata.class.getName());

    public TemplateMetadata(Map<String, Object> stringObjectMap) {
        super(stringObjectMap);
    }

    @Override
    public boolean addValidateMetadatum(String parameter, Object Value) {
        return false;
    }

    @Override
    public void applyTo(Template object) {
        //todo
    }

}
