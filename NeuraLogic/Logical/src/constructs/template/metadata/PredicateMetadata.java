package constructs.template.metadata;

import constructs.WeightedPredicate;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 5.3.18.
 */
public class PredicateMetadata extends Metadata<WeightedPredicate> {
    private static final Logger LOG = Logger.getLogger(PredicateMetadata.class.getName());

    public PredicateMetadata(Map<String, Object> pairs) {
        super(pairs);
    }

    @Override
    public boolean addValidateMetadatum(String parameterText, Object value) {
        Parameter parameter = new Parameter(parameterText);
        ParameterValue parameterValue = null;

        boolean valid = false;
        if (parameter.type == Parameter.Type.OFFSET && parameterValue.type == ParameterValue.Type.VALUE) {
            valid = true;
        }

        if (valid)
            metadata.put(parameter, parameterValue);
        else
            LOG.warning("Invalid metadata context:" + parameterText + "=" + value);
        return true;
    }

    @Override
    public void applyTo(WeightedPredicate object) {

    }


    public void addAll(PredicateMetadata predicateMetadata) {
        metadata.putAll(predicateMetadata.metadata);
    }


    public void addMetadata(PredicateMetadata predicateMetadata) {
        if (this.metadata == null) {
            this.metadata = predicateMetadata.metadata;
        } else {
            this.metadata.putAll(predicateMetadata.metadata);
        }
    }
}
