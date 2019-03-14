package constructs.template.metadata;

import ida.ilp.logic.Constant;
import networks.computation.evaluation.values.*;
import networks.structure.components.weights.Weight;

import java.util.logging.Logger;

/**
 * A dictionary of all possible values corresponding to the possible {@link Parameter} types.
 */
public class ParameterValue {
    private static final Logger LOG = Logger.getLogger(ParameterValue.class.getName());

    Type type;

    public enum Type {
        CONSTANT, WEIGHT, VALUE, INT, DOUBLE, BOOLEAN, STRING
    }

    String stringValue;
    Object value;

    public ParameterValue(Object value) {
        this.value = value;
        this.stringValue = value.toString();

        if (value instanceof Weight) {
            type = Type.WEIGHT;
        } else if (value instanceof StringValue) {
            if (stringValue.equals("true")) {
                type = Type.BOOLEAN;
                this.value = (boolean) true;
            } else if (stringValue.equals("false")) {
                type = Type.BOOLEAN;
                this.value = (boolean) false;
            } else
                type = Type.STRING;
        } else if (value instanceof Value) {
            type = Type.VALUE;
            if (value instanceof ScalarValue) {
                //todo
            } else if (value instanceof VectorValue) {

            } else if (value instanceof MatrixValue) {

            }
        } else {
            LOG.warning("Metadata value not recognized!");
        }
    }

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
