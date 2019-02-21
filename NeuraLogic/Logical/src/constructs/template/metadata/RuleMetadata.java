package constructs.template.metadata;

import constructs.template.components.WeightedRule;
import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.functions.CrossProduct;
import networks.computation.evaluation.functions.specific.Sigmoid;
import settings.Settings;

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

        } else if (parameter.type == Parameter.Type.ACTIVATION && parameterValue.type == ParameterValue.Type.STRING) {
            switch ((String) parameterValue.value) {
                case "sigmoid":
                    parameterValue.value = new Sigmoid();
                    break;
                case "crossproduct":
                    parameterValue.value = new CrossProduct(Activation.getActivationFunction(settings.ruleNeuronActivation));
            }
            //todo
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
            if (value.value instanceof CrossProduct){
                object.activationFcn = object.activationFcn != null ? new CrossProduct(object.activationFcn) : new CrossProduct(Activation.getActivationFunction(settings.ruleNeuronActivation));
            }
            object.activationFcn = (Activation) value.value;
        }
    }
}