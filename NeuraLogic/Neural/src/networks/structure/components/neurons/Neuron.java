package networks.structure.components.neurons;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.iteration.NeuronVisiting;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.structure.metadata.states.State;

import java.util.ArrayList;

public interface Neuron<T extends Neuron, S extends State.Neural> {

    void visit(NeuronVisitor.Weighted visitor);

    void visit(NeuronVisiting.Weighted visitor);

    ArrayList<T> getInputs();

    S getRawState();

    Aggregation getAggregation();

    String getId();

    int inputCount();

    boolean isShared();

    void setShared(boolean b);

    Integer getIndex();

    void setLayer(int i);

    int getLayer();

    State.Neural.Computation getComputationView(int index);
}