package networks.structure.lrnnTypes;

import networks.evaluation.functions.Activation;
import networks.evaluation.values.Value;
import networks.structure.Neuron;
import networks.structure.Weight;

/**
 * Created by gusta on 14.3.17.
 */
public class NegationNeuron extends Neuron<AtomFact> implements AtomFact {

    AtomFact input;

    public NegationNeuron(AtomFact atom, Activation negationActivation) {
        super(atom.getId());
        inputs = null;
        input = atom;
        activation = negationActivation;
    }

    @Override
    public Value evaluate() {
        return null;
    }

    @Override
    public Weight getOffset() {
        return null;
    }

    @Override
    public Value gradient() {
        return null;
    }

    @Override
    public String getId() {
        return id;
    }
}