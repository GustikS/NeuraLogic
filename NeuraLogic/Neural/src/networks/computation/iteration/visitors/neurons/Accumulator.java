package networks.computation.iteration.visitors.neurons;

import evaluation.values.ScalarValue;
import evaluation.values.Value;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.neurons.states.State;

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