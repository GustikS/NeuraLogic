package constructs.template.metadata;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 1.3.18.
 */
public abstract class Metadata {
    private static final Logger LOG = Logger.getLogger(Metadata.class.getName());

    public Map<Parameter, ParameterValue> metadata;

    public Metadata(Map<String, Object> stringObjectMap) {
        metadata = new LinkedHashMap<>();

        for (Map.Entry<String, Object> ent : stringObjectMap.entrySet()) {
            addValidateMetadatum(ent.getKey(), ent.getValue());
        }
    }

    public abstract boolean addValidateMetadatum(String parameter, Object Value);

}