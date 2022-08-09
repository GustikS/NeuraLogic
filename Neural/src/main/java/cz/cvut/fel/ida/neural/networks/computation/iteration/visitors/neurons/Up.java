package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Evaluation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.Iterator;

/**
 * Up = loading some values from inputs of actual neuron into its State - e.g. for {@link Evaluation}
 */
public class Up extends NeuronVisitor.Weighted {

    /**
     * Up = loading some values from inputs of actual neuron into its State - e.g. for {@link Evaluation}
     *
     * @param network
     * @param computationVisitor
     */
    public Up(NeuralNetwork<State.Structure> network, StateVisiting.Computation computationVisitor) {
        super(network, computationVisitor, null);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Iterator<T> inputs = network.getInputs(neuron);
        T input;
        while (inputs.hasNext()) {
            input = inputs.next();
            state.storeValue(input.getComputationView(stateVisitor.stateIndex).getValue());
        }
        Value value = stateVisitor.visit(state);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);
        Iterator<T> inputNeurons = inputs.r;
        Iterator<Weight> inputWeights = inputs.s;
        T input;
        Weight weight;

        //state.invalidate(); //todo (a) test if faster with invalidation here (at the beginning of evaluation) instead of using separate iteration with networks.computation.iteration.visitors.states.Invalidator ?

        if (neuron.offset.value != Value.ZERO)  // only store offset if it is not void - otherwise there may be problems with its behavior w.r.t. some functions
            state.storeValue(neuron.offset.value);

        while (inputNeurons.hasNext()) { //todo test version with fori
            input = inputNeurons.next();
            weight = inputWeights.next();
            state.storeValue(weight.value.times(input.getComputationView(stateVisitor.stateIndex).getValue()));
        }
        Value value = stateVisitor.visit(state);
    }
}
