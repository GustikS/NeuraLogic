package networks.structure;

import constructs.template.metadata.WeightMetadata;
import networks.evaluation.values.Value;

/**
 * Created by gusta on 8.3.17.
 */
public class Weight {
    public String name;
    public Value value;
    public boolean isFixed = false;

    double learningRate;
    public String originalString;

    WeightMetadata metadata;

    public Weight(String name, Value value, boolean fixed) {
        this.name = name;
        this.value = value;
        this.isFixed = fixed;
    }

    public Weight(Value value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return name.equals(obj);
    }

    public void init() {
        //todo
    }
}
