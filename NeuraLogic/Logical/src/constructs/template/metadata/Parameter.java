package constructs.template.metadata;

import java.util.logging.Logger;

public class Parameter {
    private static final Logger LOG = Logger.getLogger(Parameter.class.getName());

    String name;
    Type type;

    public enum Type {
        ACTIVATION, AGGREGATION, OFFSET, LEARNABLE, ADHOC
    }

    public Parameter(String parameter) {
        switch (parameter) {
            case "activation":
                type = Type.ACTIVATION;
                break;
            case "aggregation":
                type = Type.AGGREGATION;
                break;
            case "offset":
                type = Type.OFFSET;
                break;
            case "learnable":
                type = Type.LEARNABLE;
                break;
            default:
                type = Type.ADHOC;
                break;
        }
    }
}