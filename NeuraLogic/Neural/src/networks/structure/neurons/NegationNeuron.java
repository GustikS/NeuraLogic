package networks.structure.neurons;

import networks.evaluation.functions.Activation;
import networks.structure.Neuron;
import networks.structure.Weight;

/**
 * Created by gusta on 14.3.17.
 */
public class NegationNeuron extends Neuron<AtomFact> implements AtomFact {

    AtomFact input;

    public NegationNeuron(AtomFact atom, Activation negationActivation) {
        super("neg_" + atom.getId());
        inputs = null;
        input = atom;
        activation = negationActivation;
    }

    @Override
    public Weight getOffset() {
        return null;
    }

    @Override
    public String getId() {
        return id;
    }
}