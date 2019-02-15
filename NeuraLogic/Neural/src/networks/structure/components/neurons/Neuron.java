package networks.structure.components.neurons;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.iteration.NeuronVisiting;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
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

    String getId();

    int inputCount();

    boolean isShared();

    boolean makeShared(Settings settings);

    void setShared(boolean b);
}