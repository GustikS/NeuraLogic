package constructs.template.metadata;

import ida.ilp.logic.Constant;
import networks.evaluation.values.Value;
import networks.structure.weights.Weight;

import java.util.logging.Logger;

public class ParameterValue {
    private static final Logger LOG = Logger.getLogger(ParameterValue.class.getName());

    Type type;

    public enum Type {
        CONSTANT, WEIGHT, VALUE, STRING, INT, DOUBLE, BOOLEAN
    }

    String stringValue;
    Object value;

    public ParameterValue(Constant constant) {
        stringValue = constant.toString();
        value = constant;
        type = Type.CONSTANT;
    }

    public ParameterValue(Weight weight) {
        stringValue = weight.name;
        value = weight;
        type = Type.WEIGHT;
    }

    public ParameterValue(Value iValue) {
        stringValue = value.toString();
        value = iValue;
        type = Type.VALUE;
    }

    public ParameterValue(int iValue) {
        stringValue = String.valueOf(value);
        value = iValue;
        type = Type.INT;
    }

    public ParameterValue(double iValue) {
        stringValue = String.valueOf(value);
        value = iValue;
        type = Type.DOUBLE;
    }

    public ParameterValue(boolean iValue) {
        stringValue = String.valueOf(value);
        value = iValue;
        type = Type.BOOLEAN;
    }

    public ParameterValue(String iValue) {
        stringValue = String.valueOf(value);
        value = iValue;
        type = Type.STRING;
    }
}
