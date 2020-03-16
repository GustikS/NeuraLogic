package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.logging.Logger;

public class Accumulator extends NeuronVisitor.Weighted {
    private static final Logger LOG = Logger.getLogger(Accumulator.class.getName());

    public Value accumulated;

    public Accumulator(NeuralNetwork<State.Structure> network, StateVisiting.Computation computationVisitor) {
        super(network, computationVisitor, null);
        accumulated = new ScalarValue(0);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Value visit = stateVisitor.visit(state);
        accumulated.incrementBy(visit);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Value visit = stateVisitor.visit(state);
        accumulated.incrementBy(visit);
    }
}