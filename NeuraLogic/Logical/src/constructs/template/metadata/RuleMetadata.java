package constructs.template.metadata;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 1.3.18.
 */
public class RuleMetadata extends Metadata {
    private static final Logger LOG = Logger.getLogger(RuleMetadata.class.getName());

    public RuleMetadata(Map<String, Object> stringObjectMap) {
        super(stringObjectMap);
    }

    @Override
    public boolean addValidateMetadatum(String parameter, Object Value) {

        throw new NotImplementedException();
    }
}
