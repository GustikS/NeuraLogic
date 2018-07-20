package training.results;

import networks.evaluation.values.Value;
import training.NeuralSample;

import java.util.Map;

/**
 * Created by gusta on 8.3.17.
 */
public class Results {
    Map<NeuralSample, Value> outputs;

    public String toString(){
        //TODO override intelligently in subclasses
    }
}