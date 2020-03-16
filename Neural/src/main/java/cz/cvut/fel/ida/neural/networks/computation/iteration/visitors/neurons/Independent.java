package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.IndependentNeuronProcessing;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

/**
 * Separate visiting of each neuron, with no messages being passed between the neighboring neurons, e.g. for {@link IndependentNeuronProcessing}
 */
public class Independent extends NeuronVisitor.Weighted {

    public Independent(NeuralNetwork<State.Structure> network, StateVisiting.Computation computationVisitor) {
        super(network, computationVisitor, null);
    }

    @Override
    public void visit(BaseNeuron neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        stateVisitor.visit(state);
    }

    @Override
    public void visit(WeightedNeuron neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        stateVisitor.visit(state);
    }
}
