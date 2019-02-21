package constructs.template.metadata;

import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;
import settings.Settings;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 5.3.18.
 */
public class WeightMetadata extends Metadata<Weight> {
    private static final Logger LOG = Logger.getLogger(WeightMetadata.class.getName());

    public WeightMetadata(Settings settings, Map<String, Object> stringObjectMap) {
        super(settings, stringObjectMap);
    }

    @Override
    public boolean addValidateMetadatum(String parameterText, Object value) {
        Parameter parameter = new Parameter(parameterText);
        ParameterValue parameterValue = new ParameterValue(value);

        boolean valid = false;
        if (parameter.type == Parameter.Type.VALUE && parameterValue.type == ParameterValue.Type.VALUE) {
            valid = true;
        }

        if (valid)
            metadata.put(parameter, parameterValue);
        return valid;
    }

    @Override
    public void applyTo(Weight object) {
        metadata.forEach((param, value) -> apply(object, param, value));
    }

    private void apply(Weight weight, Parameter param, ParameterValue value) {
        if (param.type == Parameter.Type.VALUE) {
            weight.value = (Value) value.value;
        }
    }

    public void addAll(WeightMetadata weightMetadata) {
        if (this.metadata == null) {
            this.metadata = weightMetadata.metadata;
        } else {
            this.metadata.putAll(weightMetadata.metadata);
        }
    }
}