package networks.evaluation.results;

import networks.evaluation.values.Value;
import training.NeuralSample;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Results {
    Map<NeuralSample, Value> outputs = new HashMap<>();

    public abstract String toString();

    public void addResult(NeuralSample sample, Value value){
        outputs.put(sample,value);
    }

    public abstract boolean calculate();
}