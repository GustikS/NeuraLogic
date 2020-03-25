package cz.cvut.fel.ida.neural.networks.structure.components.neurons;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.NeuronVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.ArrayList;

public interface Neurons<T extends Neurons, S extends State.Neural> extends Exportable {

    void visit(NeuronVisitor.Weighted visitor);

    void visit(NeuronVisiting.Weighted visitor);

    void visit(NeuronVisitor.Weighted.Detailed visitor);

    ArrayList<T> getInputs();

    void addInput(T neuron);

    S getRawState();

    Aggregation getAggregation();

    String getName();

    int inputCount();

    boolean isShared();

    void setShared(boolean b);

    Integer getIndex();

    void setLayer(int i);

    int getLayer();

    State.Neural.Computation getComputationView(int index);
}