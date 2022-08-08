package cz.cvut.fel.ida.logic.constructs.template.metadata;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.utils.metadata.Metadata;
import cz.cvut.fel.ida.algebra.utils.metadata.Parameter;
import cz.cvut.fel.ida.algebra.utils.metadata.ParameterValue;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.setup.Settings;

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
        } else if (parameter.type == Parameter.Type.TRANSFORMATION && parameterValue.type == ParameterValue.Type.STRING) {
            Settings.TransformationFcn transformationFcn = Settings.parseTransformation(parameterValue.stringValue);
            Transformation function = Transformation.getFunction(transformationFcn);
            if (function != null) {
                valid = true;
                parameterValue.value = function;
            }
        } else if ((parameter.type == Parameter.Type.AGGREGATION || parameter.type == Parameter.Type.COMBINATION) && parameterValue.type == ParameterValue.Type.STRING) {
            Settings.CombinationFcn combinationFcn = Settings.parseCombination(parameterValue.stringValue);
            Combination function = Combination.getFunction(combinationFcn);
            if (function != null) {
                valid = true;
                parameterValue.value = function;
            }
        }
        //todo rest
        if (valid)
            metadata.put(parameter, parameterValue);
        return true;
    }

    @Override
    public void applyTo(WeightedRule object) {
        metadata.forEach((param, value) -> apply(object, param, value));
    }

    private void apply(WeightedRule rule, Parameter param, ParameterValue value) {
        if (param.type == Parameter.Type.COMBINATION) {
            rule.setCombination((Combination) value.value);
        }
        if (param.type == Parameter.Type.TRANSFORMATION) {
            rule.setTransformation((Transformation) value.value);
        }
        if (param.type == Parameter.Type.AGGREGATION) {
            rule.setAggregationFcn((Aggregation) value.value);
        }
    }
}