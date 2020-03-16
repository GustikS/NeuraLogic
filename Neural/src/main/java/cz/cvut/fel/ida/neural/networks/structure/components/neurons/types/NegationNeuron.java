package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.ArrayList;

/**
 * Created by gusta on 14.3.17.
 */
public class NegationNeuron<S extends State.Neural> extends BaseNeuron<AtomFact, S> implements AtomFact<AtomFact, S> {

    public NegationNeuron(AtomFact atom, int index, S state) {
        super(index, "neg_" + atom.getName(), state);
        inputs = new ArrayList<>(1);
        inputs.add(atom);
    }

    @Override
    public final Weight getOffset() {
        return null;
    }

    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }
}