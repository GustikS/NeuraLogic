package networks.computation.iteration.visitors.neurons;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.actions.Backpropagation;
import networks.computation.iteration.actions.Evaluation;
import networks.computation.iteration.actions.IndependentNeuronProcessing;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;
import utils.generic.Pair;

import java.util.Iterator;

/**
 * Some classic implementations of {@link NeuronVisitor} for standard uses. That is for going:
 *  - Up = loading some values from inputs of actual neuron into its State - e.g. for {@link Evaluation}
 *  - Down = propagating some values from actual neuron's State to its inputs - e.g. for ({@link Backpropagation}
 *
 *  The actual values/messages being propagated between the neurons are calculated by the corresponding {@link NeuronVisitor#stateVisitor}.
 *
 *  These visitors DO NOT support input masking (i.e. max pooling).
 *
 * @see NeuronVisitor
 */
public class StandardNeuronVisitors {

    public static class Up extends NeuronVisitor.Weighted {

        /**
         * Up = loading some values from inputs of actual neuron into its State - e.g. for {@link Evaluation}
         * @param network
         * @param computationVisitor
         */
        public Up(NeuralNetwork<State.Neural.Structure> network, StateVisiting.Computation computationVisitor) {
            super(network, computationVisitor, null);
        }

        @Override
        public void visit(BaseNeuron neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Iterator<BaseNeuron> inputs = network.getInputs(neuron);
            for (BaseNeuron input; (input = inputs.next()) != null; ) {
                state.store(stateVisitor, input.getComputationView(stateVisitor.stateIndex).getResult(stateVisitor));
            }
            Value value = stateVisitor.visit(state);
        }

        @Override
        public void visit(WeightedNeuron neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Pair<Iterator<BaseNeuron>, Iterator<Weight>> inputs = network.getInputs(neuron);
            Iterator<BaseNeuron> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            BaseNeuron input;
            Weight weight;

            //state.invalidate(); //todo (a) test if faster with invalidation here (at the beginning of evaluation) instead of using separate iteration with networks.computation.iteration.visitors.states.Invalidator ?
            state.store(stateVisitor, neuron.offset.value);
            while ((input = inputNeurons.next()) != null && (weight = inputWeights.next()) != null) { //todo test version with fori
                state.store(stateVisitor, input.getComputationView(stateVisitor.stateIndex).getResult(stateVisitor).times(weight.value));
            }
            Value value = stateVisitor.visit(state);
        }
    }

    /**
     * Down = propagating some values from actual neuron's State to its inputs - e.g. for ({@link Backpropagation}
     *
     * These visitors DO NOT support input masking (i.e. max pooling).
     */
    public static class Down extends NeuronVisitor.Weighted {

        StateVisiting.Computation bottomUp;

        public Down(NeuralNetwork<State.Neural.Structure> network, StateVisiting.Computation topDown, StateVisiting.Computation bottomUp, WeightUpdater weightUpdater) {
            super(network, topDown, weightUpdater);
            this.bottomUp = bottomUp;
        }

        @Override
        public void visit(BaseNeuron neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value value = stateVisitor.visit(state);

            Iterator<BaseNeuron> inputs = network.getInputs(neuron);

            for (BaseNeuron input; (input = inputs.next()) != null; ) {
                input.getComputationView(stateVisitor.stateIndex).store(stateVisitor, value);
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
            while ((input = inputNeurons.next()) != null && (weight = inputWeights.next()) != null) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);

                weightUpdater.visit(weight, gradient.times(computationView.getResult(bottomUp)));
                computationView.store(stateVisitor, gradient.times(weight.value));
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
        void visit(WeightedNeuron neuron) {

        }
    }
}
