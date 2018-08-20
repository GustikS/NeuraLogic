package networks.structure;

import constructs.template.metadata.WeightMetadata;
import networks.evaluation.values.Value;

/**
 * Created by gusta on 8.3.17.
 */
public class Weight {
    public String name;
    public Value value;
    public boolean isLearnable = true;

    double learningRate;
    public String originalString;

    WeightMetadata metadata;

    public Weight(String name, Value value, boolean fixed) {
        this.name = name;
        this.value = value;
        this.isLearnable = !fixed;
    }
}
