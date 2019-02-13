package networks.structure.components.neurons;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.iteration.NeuronVisiting;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.ArrayList;

public interface Neuron {

    default void visit(NeuronVisitor visitor) {
        visitor.visit(this);
    }

    default void visit(NeuronVisiting visitor) {
        visitor.visit(this);
    }

    ArrayList<? extends Neuron> getInputs();

    Aggregation getAggregation();

    State.Neural.Computation getBaseState(Settings settings);

    String getId();

    int inputCount();
}