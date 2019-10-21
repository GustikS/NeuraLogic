package networks.computation.iteration.visitors.neurons;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.actions.Backpropagation;
import networks.computation.iteration.actions.Evaluation;
import networks.computation.iteration.actions.IndependentNeuronProcessing;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;
import utils.generic.Pair;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Some classic implementations of {@link NeuronVisitor} for standard uses. That is for going:
 * - Up = loading some values from inputs of actual neuron into its State - e.g. for {@link Evaluation}
 * - Down = propagating some values from actual neuron's State to its inputs - e.g. for ({@link Backpropagation}
 * <p>
 * The actual values/messages being propagated between the neurons are calculated by the corresponding {@link NeuronVisitor#stateVisitor}.
 * <p>
 * These visitors DO NOT support input masking (i.e. max pooling).
 *
 * @see NeuronVisitor
 */
public class StandardNeuronVisitors {
    private static final Logger LOG = Logger.getLogger(StandardNeuronVisitors.class.getName());

    public static class Up extends NeuronVisitor.Weighted {

        /**
         * Up = loading some values from inputs of actual neuron into its State - e.g. for {@link Evaluation}
         *
         * @param network
         * @param computationVisitor
         */
        public Up(NeuralNetwork<State.Neural.Structure> network, StateVisiting.Computation computationVisitor) {
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
            state.storeValue(neuron.offset.value);
            while (inputNeurons.hasNext()) { //todo test version with fori
                input = inputNeurons.next();
                weight = inputWeights.next();
                state.storeValue(weight.value.times(input.getComputationView(stateVisitor.stateIndex).getValue()));
            }
            Value value = stateVisitor.visit(state);
        }
    }

    /**
     * Down = propagating some values from actual neuron's State to its inputs - e.g. for ({@link Backpropagation}
     * <p>
     * These visitors DO NOT support input masking (i.e. max pooling).
     */
    public static class Down extends NeuronVisitor.Weighted {

        public Down(NeuralNetwork<State.Neural.Structure> network, StateVisiting.Computation topDown, WeightUpdater weightUpdater) {
            super(network, topDown, weightUpdater);
        }

        @Override
        public void visit(BaseNeuron neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);

            Iterator<BaseNeuron> inputs = network.getInputs(neuron);

            BaseNeuron input;
            while (inputs.hasNext()) {
                input = inputs.next();
                input.getComputationView(stateVisitor.stateIndex).storeGradient(gradient);
            }
        }

        @Override
        public void visit(WeightedNeuron neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
            //state.invalidate(); //todo (b) test if faster with invalidation here (at the end of backprop) instead of using separate iteration with networks.computation.iteration.visitors.states.Invalidator ?
            Pair<Iterator<BaseNeuron>, Iterator<Weight>> inputs = network.getInputs(neuron);

            weightUpdater.visit(neuron.offset, gradient);

            Iterator<BaseNeuron> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            BaseNeuron input;
            Weight weight;
            while (inputNeurons.hasNext()) {    //neurons and weights should always be aligned correctly, so skipping the check here for some speedup
                input = inputNeurons.next();
                weight = inputWeights.next();
                State.Neural.Computation inputComputationView = input.getComputationView(stateVisitor.stateIndex);

                weightUpdater.visit(weight, gradient.times(inputComputationView.getValue()));
                inputComputationView.storeGradient(gradient.times(weight.value));
            }
        }
    }

    /**
     * Separate visiting of each neuron, with no messages being passed between the neighboring neurons, e.g. for {@link IndependentNeuronProcessing}
     */
    public static class Independent extends NeuronVisitor.Weighted {

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
}
