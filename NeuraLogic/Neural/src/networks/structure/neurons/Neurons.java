package networks.structure.neurons;

import networks.evaluation.functions.Activation;
import networks.structure.Neuron;

import java.util.ArrayList;

public interface Neurons {

    <T extends Neuron> ArrayList<T> getInputs();

    Activation getActivation();

    String getId();

    int inputCount();

}