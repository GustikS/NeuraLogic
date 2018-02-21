package networks.structure;

import networks.evaluation.functions.Activation;
import networks.evaluation.values.Value;

import java.util.Map;

/**
 * Created by gusta on 8.3.17.
 */
public class Neuron {
    String id;

    Value output;
    Map<Neuron, Weight> inputs;
    Activation activation;
}
