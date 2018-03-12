package networks.structure;

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

    public void init() {

    }

    public static Weight construct(String name, Value value, boolean fixed) {
        //TODO weak factory method
        Weight weight = new Weight();
        weight.name = name;
        weight.value = value;
        weight.isLearnable = !fixed;
        return weight;
    }
}
