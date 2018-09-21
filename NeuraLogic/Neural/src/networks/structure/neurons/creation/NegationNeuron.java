package networks.structure.neurons.creation;

import networks.evaluation.functions.Activation;
import networks.evaluation.iteration.State;
import networks.structure.weights.Weight;
import networks.structure.neurons.Neuron;

import java.util.ArrayList;

/**
 * Created by gusta on 14.3.17.
 */
public class NegationNeuron<S extends State> extends Neuron<AtomFact, S> implements AtomFact {

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