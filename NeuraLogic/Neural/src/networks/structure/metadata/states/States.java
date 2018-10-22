package networks.structure.metadata.states;

import networks.computation.iteration.actions.Backprop;
import networks.computation.iteration.actions.StateVisitor;
import networks.computation.training.evaluation.values.Value;
import networks.structure.metadata.inputMappings.NeuronMapping;
import networks.structure.components.neurons.Neuron;

import java.util.logging.Logger;

/**
 * An agglomeration of special classes for storing computational and structural States (see interface State) of Neurons.
 */
public class States implements State {
    private static final Logger LOG = Logger.getLogger(States.class.getName());

    /**
     * A typical, lightweight State that consists of summed inputs (before activation), output value (after activation), and gradient (before activation) todo check
     * Even though Evaluation and Backprop are always carried out separately, and so it seems that a single Value placeholder
     * could be stored here, value and gradient must be held as two separate Values, since Backprop needs both to calculate gradient.
     */
    public static class InputsOutputGradient<V extends Value> implements Computation {
        V summedInputs;
        V outputValue;
        V acumGradient;

        @Override
        public void invalidate() {
            summedInputs.zero();
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
        public void incrementGradient(Backprop backprop, Value value) {
            acumGradient.increment(value);
            checked++;
        }

        public <V extends Value> boolean ready4expansion(Backprop vStateVisitor) {
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

        @Override
        public <V> V accept(StateVisitor<V> visitor) {
            return states[visitor.state_index].accept(visitor);
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