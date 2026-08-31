package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.NeuronVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.States;

import java.util.ArrayList;

/**
 * Created by gusta on 8.3.17.
 */
public class FactNeuron extends WeightedNeuron<BaseNeuron, States.SimpleValue> implements AtomFact<BaseNeuron, States.SimpleValue> {

    public boolean hasLearnableValue;

    /**
     * The ground literal this fact stands for, kept only when the fact carries a *learnable* value.
     * <p>
     * Such a value is a parameter of the example rather than of the model - its {@link Weight} comes from a
     * factory run after the model was built, so it is not in
     * {@link cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel#allWeights} and nothing that
     * saves a model can see it. Saving it needs a name, and the neuron's own name will not do: that is the
     * fact's original string, which carries the value it was written with, so it changes when the same
     * example is rebuilt from a different starting point.
     * <p>
     * Null on every other fact neuron, deliberately. A real problem grounds millions of them and this is a
     * fresh string per neuron, where the name is one that already existed; only the facts that actually
     * carry a parameter pay for it. Null also distinguishes an *example* fact from a template one, which is
     * the model's own and already saved.
     */
    public String factLiteral;

    public FactNeuron(String fact, Weight offset, int index, States.SimpleValue state) {
        super(fact, index, state, offset);
    }

    public void visit(NeuronVisitor.Weighted visitor) {
        if (hasLearnableValue) {    // only factNeurons with a learnable value are actionable by some visitors (otherwise they are just a simple constant Value storage)
            visitor.visit(this);
        }
    }

    public void visit(NeuronVisiting.Weighted visitor) {
        if (hasLearnableValue) {    // only factNeurons with a learnable value are actionable by some visitors (otherwise they are just a simple constant Value storage)
            visitor.visit(this);
        }
    }

    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }
}