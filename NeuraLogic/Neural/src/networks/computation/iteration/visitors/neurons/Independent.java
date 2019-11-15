package networks.computation.iteration.visitors.neurons;

import networks.computation.iteration.actions.IndependentNeuronProcessing;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.metadata.states.State;

/**
 * Separate visiting of each neuron, with no messages being passed between the neighboring neurons, e.g. for {@link IndependentNeuronProcessing}
 */
public class Independent extends NeuronVisitor.Weighted {

    public Independent(NeuralNetwork<State.Neural.Structure> network, StateVisiting.Computation computationVisitor) {
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
