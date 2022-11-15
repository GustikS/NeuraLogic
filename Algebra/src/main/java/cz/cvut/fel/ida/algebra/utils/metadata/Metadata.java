package cz.cvut.fel.ida.algebra.utils.metadata;

import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 1.3.18.
 */
public abstract class Metadata<T> implements Exportable {
    private static final Logger LOG = Logger.getLogger(Metadata.class.getName());

    protected Settings settings;

    protected Map<Parameter, ParameterValue> metadata;

    protected Metadata() {
    }

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

    public static Map<String, Object> merge(Map<String, Object> set1, Map<String, Object> set2) {
        set1.putAll(set2);
        return set1;
    }

    public Object getByName(String name) {
        ParameterValue parameterValue = metadata.get(new Parameter(name));
        if (parameterValue != null)
            return parameterValue.value;
        else
            return null;
    }

    public void put(Parameter parameter, ParameterValue parameterValue) {
        this.metadata.put(parameter, parameterValue);
    }
}