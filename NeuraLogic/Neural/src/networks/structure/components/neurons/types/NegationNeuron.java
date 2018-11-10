package networks.structure.components.neurons.types;

import networks.computation.evaluation.functions.Activation;
import networks.structure.metadata.states.State;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.weights.Weight;

import java.util.ArrayList;

/**
 * Created by gusta on 14.3.17.
 */
public class NegationNeuron<S extends State.Neural> extends Neuron<AtomFact, S> implements AtomFact {

    public NegationNeuron(AtomFact atom, int index, S state, Activation negationActivation) {
        super(index, "neg_" + atom.getId(), state, negationActivation);
        inputs = new ArrayList<>(1);
        inputs.add(atom);
    }

    @Override
    public final Weight getOffset() {
        return null;
    }
}