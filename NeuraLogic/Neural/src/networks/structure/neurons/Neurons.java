package networks.structure.neurons;

import networks.computation.functions.Activation;
import networks.computation.iteration.actions.NeuronVisitor;

import java.util.ArrayList;

public interface Neurons {

    default <V> V accept(NeuronVisitor<V> visitor) {
        return visitor.visit(this);
    }

    ArrayList<? extends Neurons> getInputs();

    Activation getActivation();

    String getId();

    int inputCount();

}