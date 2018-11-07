package networks.structure.metadata.states;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.actions.Backproper;
import networks.computation.iteration.actions.StateVisitor;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.inputMappings.NeuronMapping;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * An agglomeration of special classes for storing computational and structural States (see interface State) of Neurons.
 */
public class States implements State {
    private static final Logger LOG = Logger.getLogger(States.class.getName());

    /**
     * A typical, minimal, lightweight State that consists of summed inputs (before activation), output value (after activation), and gradient (before activation) todo check
     * Even though Evaluation and Backprop are always carried out separately, and so it seems that a single Value placeholder
     * could be stored here, value and gradient must be held as two separate Values, since Backprop needs both to calculate gradient.
     */
    public static class InputsOutputGradient<V extends Value> implements Computation {
        /**
         * In the most general case, we need to feed the whole list of inputs into the activation function, not just the sum.
         * E.g. for max/avg, although they could still have functions to calculate cumulatively without storing all the inputs,
         * there could possibly be functions that cannot be calculated in a cumulative fashion, e.g. when we need to process all elements twice (e.g. normalize/softmax ?).
         */
        ArrayList<V> accumulatedInputs;
        V outputValue;
        V acumGradient;

        @Override
        public void invalidate() {
            accumulatedInputs.clear();
            outputValue.zero();
            acumGradient.zero();
        }
    }

    /**
     * Simple storage of parent count for efficient backprop computation with DFS (may vary due to neuron sharing in different contexts).
     */
    public static final class ParentCounter extends InputsOutputGradient<Value> {
        public final int count;
        public int checked = 0;

        public ParentCounter(int count) {
            this.count = count;
        }

        //todo move this out to visitor
        public void incrementGradient(Backproper backproper, Value value) {
            acumGradient.increment(value);
            checked++;
        }

        public <V extends Value> boolean ready4expansion(Backproper vStateVisitor) {
            return checked >= count;
        }
    }

    /**
     * Storing a State that is an array of other States, e.g. for parallel mini-batch processing, where different views of the same neuron may operate with different states over that single neuron.
     *
     * @param <T>
     */
    public static final class StateComposite<T extends State.Computation> implements State.Computation {
        public final T[] states;

        public StateComposite(T[] states) {
            this.states = states;
        }

        //todo implement all the methods redirection here, or create separate visitors for each method.
        @Override
        public <V> V accept(StateVisitor<V> visitor) {
            return states[visitor.stateIndex].accept(visitor);
        }

        @Override
        public void invalidate() {
            for (int i = 0; i < states.length; i++) {
                states[i].invalidate();
            }
        }
    }

    /**
     * Storing inputs and outputs of each neuron (may vary due to neuron sharing in different contexts).
     * This information should be stored in a Network (not Neuron).
     */
    public static final class InputsOutputsPair implements State.Structure {
        NeuronMapping<Neuron> inputs;
        NeuronMapping<Neuron> outputs;


        public final NeuronMapping getOutputs() {
            return outputs;
        }

        public NeuronMapping<Neuron> getInputs(Neuron neuron) {
            return inputs;
        }
    }

}