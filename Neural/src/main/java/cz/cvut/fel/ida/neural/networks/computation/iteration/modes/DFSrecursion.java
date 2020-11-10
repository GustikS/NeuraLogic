package cz.cvut.fel.ida.neural.networks.computation.iteration.modes;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.BottomUp;
import cz.cvut.fel.ida.neural.networks.computation.iteration.NeuronVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.TopDown;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This mode is well suited for visitors, where the actions are carried out during the recursive calls.
 * However this mode is not suited for iterators, as the state of iteration is no explicitly held, so the state is lost
 * during subsequent next() calls.
 */
public class DFSrecursion {
    private static final Logger LOG = Logger.getLogger(DFSrecursion.class.getName());

    public class TDownVisitor extends NeuronVisiting.Weighted implements TopDown {

        StateVisiting.Computation stateVisitor;
        WeightUpdater weightUpdater;

        public TDownVisitor(NeuralNetwork<State.Neural.Structure> network, Neurons neuron, StateVisiting.Computation topDown, WeightUpdater weightUpdater) {
            super(network, neuron);
            this.weightUpdater = weightUpdater;
            this.stateVisitor = topDown;
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
//            Iterator<T> inputs = network.getInputs(neuron, state.getAggregationState().getInputMask());
            Iterator<T> inputs = network.getInputs(neuron);
            while (inputs.hasNext()) {
                T input = inputs.next();
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);
                computationView.storeGradient(gradient);
                if (computationView.ready4expansion(stateVisitor)) {    //we can check the children in advance here since we expanded them already ourselves
                    input.visit(this);
                }
            }
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
//            Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron, state.getAggregationState().getInputMask());   //todo changed during debugging
            Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);
            weightUpdater.visit(neuron.offset, gradient);

            Iterator<T> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            T input;
            Weight weight;
            while (inputNeurons.hasNext() && inputWeights.hasNext()) {
                input = inputNeurons.next();
                weight = inputWeights.next();
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);

                weightUpdater.visit(weight, gradient.times(computationView.getValue().transposedView()));    //todo speedup multiply this after checking if learnable
                computationView.storeGradient(gradient.transposedView().times(weight.value));

                if (computationView.ready4expansion(stateVisitor)) {
                    input.visit(this);
                }
            }
        }

        @Override
        public void topdown() {
            outputNeuron.visit(this);
        }

    }

    public class BUpVisitor extends NeuronVisiting.Weighted implements BottomUp<Value> {
        NeuronVisitor.Weighted neuronVisitor;
        StateVisiting.Computation stateVisitor;

        public BUpVisitor(NeuralNetwork<State.Neural.Structure> network, Neurons outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(network, outputNeuron);
            this.neuronVisitor = pureNeuronVisitor;
            this.stateVisitor = pureNeuronVisitor.stateVisitor;
        }

        @Override
        public Value bottomUp() {
            outputNeuron.visit(this);
            return outputNeuron.getComputationView(stateVisitor.stateIndex).getValue();
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            //1st evaluate all the inputs
            Iterator<T> inputs = network.getInputs(neuron);
            while (inputs.hasNext()) {
                T input = inputs.next();
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);
                if (!computationView.ready4expansion(stateVisitor))     // is not yet evaluated?
                    input.visit(this);
            }
            //2nd evaluate current neuron from its inputs
            neuron.visit(neuronVisitor);
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);
            Iterator<T> inputNeurons = inputs.r;
            //1st evaluate all the inputs
            while (inputNeurons.hasNext()) {
                T input = inputNeurons.next();
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);
                if (!computationView.ready4expansion(stateVisitor))     // is not yet evaluated?
                    input.visit(this);
            }

            //2nd evaluate current neuron from its inputs
            neuron.visit(neuronVisitor);
        }


    }
}
