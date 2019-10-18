package constructs.template.metadata;

import constructs.WeightedPredicate;
import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;
import settings.Settings;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 5.3.18.
 */
public class PredicateMetadata extends Metadata<WeightedPredicate> {
    private static final Logger LOG = Logger.getLogger(PredicateMetadata.class.getName());

    public PredicateMetadata(Settings settings, Map<String, Object> pairs) {
        super(settings, pairs);
    }

    @Override
    public boolean addValidateMetadatum(String parameterText, Object value) {
        Parameter parameter = new Parameter(parameterText);
        ParameterValue parameterValue = new ParameterValue(value);

        boolean valid = false;
        if (parameter.type == Parameter.Type.OFFSET && (parameterValue.type == ParameterValue.Type.VALUE || parameterValue.type == ParameterValue.Type.WEIGHT)) {
            valid = true;
        } else if (parameter.type == Parameter.Type.ACTIVATION && parameterValue.type == ParameterValue.Type.STRING) {
            Aggregation aggregation = Activation.parseActivation(parameterValue.stringValue);
            if (aggregation != null) {
                valid = true;
                parameterValue.value = aggregation;
            }
            //todo rest
        }

        if (valid)
            metadata.put(parameter, parameterValue);
        else
            LOG.warning("Invalid metadata context:" + parameterText + "=" + value);
        return true;
    }

    @Override
    public void applyTo(WeightedPredicate object) {
        metadata.forEach((param, value) -> apply(object, param, value));
    }

    private void apply(WeightedPredicate predicate, Parameter param, ParameterValue value) {
        if (param.type == Parameter.Type.ACTIVATION) {
            predicate.activation = (Activation) value.value;
        } else if (param.type == Parameter.Type.OFFSET) {
            if (value.value instanceof Weight)
                predicate.weight = (Weight) value.value;
            else if (value.value instanceof Value)
                //we just crudely create a new weight for this offset (because we know that it is not shared anywhere is the template, otherwise it would get parsed as a weight object)
                predicate.weight = new Weight(-1, "metaOffset", (Value) value.value, false, true);
        }
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
