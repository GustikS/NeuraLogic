package networks.computation.iteration.modes;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.BottomUp;
import networks.computation.iteration.NeuronVisiting;
import networks.computation.iteration.TopDown;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;
import utils.generic.Pair;

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

        public TDownVisitor(NeuralNetwork<State.Neural.Structure> network, BaseNeuron<Neuron, State.Neural> neuron, StateVisiting.Computation topDown, WeightUpdater weightUpdater) {
            super(network, neuron);
            this.weightUpdater = weightUpdater;
            this.stateVisitor = topDown;
        }

        @Override
        public <T extends Neuron, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
            Iterator<T> inputs = network.getInputs(neuron, state.getAggregationState().getInputMask());
            for (T input; (input = inputs.next()) != null; ) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);
                computationView.storeGradient(gradient);
                if (computationView.ready4expansion(stateVisitor)) {    //we can check the children in advance here since we expanded them already ourselves
                    input.visit(this);
                }
            }
        }

        @Override
        public <T extends Neuron, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
            Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron, state.getAggregationState().getInputMask());

            weightUpdater.visit(neuron.offset, gradient);

            Iterator<T> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            T input;
            Weight weight;
            while ((input = inputNeurons.next()) != null && (weight = inputWeights.next()) != null) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);

                weightUpdater.visit(weight, gradient.times(computationView.getValue()));
                computationView.storeGradient(gradient.times(weight.value));

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

        public BUpVisitor(NeuralNetwork<State.Neural.Structure> network, BaseNeuron<Neuron, State.Neural> outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
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
        public <T extends Neuron, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            //1st evaluate all the inputs
            Iterator<T> inputs = network.getInputs(neuron);
            for (T input; (input = inputs.next()) != null; ) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);
                if (!computationView.ready4expansion(stateVisitor))     // is not yet evaluated?
                    input.visit(this);
            }
            //2nd evaluate current neuron from its inputs
            neuron.visit(neuronVisitor);
        }

        @Override
        public <T extends Neuron, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);
            Iterator<T> inputNeurons = inputs.r;
            //1st evaluate all the inputs
            for (T input; (input = inputNeurons.next()) != null; ) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);
                if (!computationView.ready4expansion(stateVisitor))     // is not yet evaluated?
                    input.visit(this);
            }

            //2nd evaluate current neuron from its inputs
            neuron.visit(neuronVisitor);
        }


    }
}
