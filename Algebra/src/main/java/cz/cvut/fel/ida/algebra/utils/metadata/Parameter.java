package cz.cvut.fel.ida.algebra.utils.metadata;

import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.logging.Logger;

/**
 * A dictionary for all possible metadata parameter names.
 */
public class Parameter implements Exportable {
    private static final Logger LOG = Logger.getLogger(Parameter.class.getName());

    String name;
    public Type type;

    public enum Type {
        ACTIVATION,
        AGGREGATION,
        COMBINATION,
        NEGATION,
        OFFSET,
        LEARNABLE,
        VALUE,      //e.g. for storing images as default values of ground literals through weights ($w1 image(1). $w2 [value = (1,0,10,....)])
        ADHOC
    }

    public Parameter(String parameter) {
        name = parameter;
        switch (parameter) {
            case "activation":
                type = Type.ACTIVATION;
                break;
            case "transformation":
                type = Type.ACTIVATION;
                break;
            case "aggregation":
                type = Type.AGGREGATION;
                break;
            case "combination":
                type = Type.COMBINATION;
                break;
            case "negation":
                type = Type.NEGATION;
                break;
            case "offset":
                type = Type.OFFSET;
                break;
            case "learnable":
                type = Type.LEARNABLE;
                break;
            case "value":
                type = Type.VALUE;
                break;
            case "initValue":
                type = Type.VALUE;
                break;
            default:
                type = Type.ADHOC;
                break;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Parameter) {
            if (name.equals(((Parameter) obj).name)) {
                return true;
            }
        }
        return false;
    }
}