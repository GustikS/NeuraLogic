package constructs.template.metadata;

import constructs.template.components.WeightedRule;
import evaluation.functions.Activation;
import evaluation.functions.Aggregation;
import evaluation.functions.CrossProduct;
import settings.Settings;
import utils.metadata.Metadata;
import utils.metadata.Parameter;
import utils.metadata.ParameterValue;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 1.3.18.
 */
public class RuleMetadata extends Metadata<WeightedRule> {
    private static final Logger LOG = Logger.getLogger(RuleMetadata.class.getName());

    public RuleMetadata(Settings settings, Map<String, Object> stringObjectMap) {
        super(settings, stringObjectMap);
    }

    @Override
    public boolean addValidateMetadatum(String parameterText, Object value) {
        Parameter parameter = new Parameter(parameterText);
        ParameterValue parameterValue = new ParameterValue(value);

        boolean valid = false;
        if (parameter.type == Parameter.Type.OFFSET && parameterValue.type == ParameterValue.Type.VALUE) {
            valid = true;
        } else if (parameter.type == Parameter.Type.LEARNABLE && parameterValue.type == ParameterValue.Type.BOOLEAN) {
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
        return true;
    }

    @Override
    public void applyTo(WeightedRule object) {
        metadata.forEach((param, value) -> apply(object, param, value));
    }

    private void apply(WeightedRule object, Parameter param, ParameterValue value) {
        if (param.type == Parameter.Type.ACTIVATION) {
            if (value.value instanceof CrossProduct) {
                object.setActivationFcn(object.getActivationFcn() != null ? new CrossProduct(object.getActivationFcn()) : new CrossProduct(Activation.getActivationFunction(settings.ruleNeuronActivation)));
            }
            object.setActivationFcn((Activation) value.value);
        }
    }
}