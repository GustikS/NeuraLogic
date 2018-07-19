package constructs.template.metadata;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 5.3.18.
 */
public class WeightMetadata extends Metadata {
    private static final Logger LOG = Logger.getLogger(WeightMetadata.class.getName());

    public WeightMetadata(Map<String, Object> stringObjectMap) {
        super(stringObjectMap);
    }

    @Override
    public boolean addValidateMetadatum(String parameter, Object Value) {
        return false;
    }
}
