package cz.cvut.fel.ida.logic.constructs.template.metadata;

import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.utils.metadata.Metadata;
import cz.cvut.fel.ida.algebra.utils.metadata.Parameter;
import cz.cvut.fel.ida.algebra.utils.metadata.ParameterValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.constructs.WeightedPredicate;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Map;
import java.util.logging.Logger;

//import cz.cvut.fel.ida.algebra.utils.metadata.Metadata;

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
        } else if (parameter.type == Parameter.Type.TRANSFORMATION && parameterValue.type == ParameterValue.Type.STRING) {
            Settings.TransformationFcn transformationFcn = Settings.parseTransformation(parameterValue.stringValue);
            Transformation function = Transformation.getFunction(transformationFcn);
            if (function != null) {
                valid = true;
                parameterValue.value = function;
            }
        } else if (parameter.type == Parameter.Type.COMBINATION && parameterValue.type == ParameterValue.Type.STRING) {
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
        else
            LOG.warning("Invalid metadata context:" + parameterText + "=" + value);
        return true;
    }

    @Override
    public void applyTo(WeightedPredicate object) {
        metadata.forEach((param, value) -> apply(object, param, value));
    }

    private void apply(WeightedPredicate predicate, Parameter param, ParameterValue value) {
        if (param.type == Parameter.Type.TRANSFORMATION) {
            predicate.transformation = (Transformation) value.value;
        } else if (param.type == Parameter.Type.COMBINATION) {
            predicate.combination = (Combination) value.value;
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
