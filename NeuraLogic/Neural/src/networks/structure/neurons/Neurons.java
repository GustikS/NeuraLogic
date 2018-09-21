package networks.structure.neurons;

import networks.evaluation.functions.Activation;

import java.util.ArrayList;

public interface Neurons {

    ArrayList<? extends Neurons> getInputs();

    Activation getActivation();

    String getId();

    int inputCount();

}