package networks.structure.components.neurons;

import networks.computation.evaluation.functions.Activation;
import networks.computation.iteration.NeuronVisiting;
import networks.computation.iteration.NeuronVisitor;

import java.util.ArrayList;

public interface Neurons {

    default void visit(NeuronVisitor visitor) {
        visitor.visit(this);
    }

    default void visit(NeuronVisiting visitor) {
        visitor.visit(this);
    }

    ArrayList<? extends Neurons> getInputs();

    Activation getActivation();

    String getId();

    int inputCount();

}