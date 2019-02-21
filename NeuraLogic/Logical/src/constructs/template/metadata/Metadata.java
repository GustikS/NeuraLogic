package constructs.template.metadata;

import settings.Settings;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 1.3.18.
 */
public abstract class Metadata<T> {
    private static final Logger LOG = Logger.getLogger(Metadata.class.getName());

    Settings settings;

    public Map<Parameter, ParameterValue> metadata;

    public Metadata(Settings settings, Map<String, Object> stringObjectMap) {
        this.settings = settings;
        metadata = new LinkedHashMap<>();

        for (Map.Entry<String, Object> ent : stringObjectMap.entrySet()) {
            addValidateMetadatum(ent.getKey(), ent.getValue());
        }
    }

    public abstract boolean addValidateMetadatum(String parameter, Object Value);

    public abstract void applyTo(T object);

    public static Metadata addAll(Metadata o, Metadata o1) {
        o.metadata.putAll(o1.metadata);
        return o;
    }

    public static Map<String,Object> merge(Map<String,Object> set1, Map<String,Object> set2) {
        set1.putAll(set2);
        return set1;
    }
}