package networks.structure.components.neurons;

import networks.computation.evaluation.functions.Activation;
import networks.computation.iteration.NeuronVisiting;
import networks.computation.iteration.NeuronVisitor;

import java.util.ArrayList;

public interface Neurons {

    default void propagate(NeuronVisitor visitor) {
        visitor.propagate(this);
    }

    default void expand(NeuronVisiting visitor) {
        visitor.expand(this);
    }

    ArrayList<? extends Neurons> getInputs();

    Activation getActivation();

    String getId();

    int inputCount();

}