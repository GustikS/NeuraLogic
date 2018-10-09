package networks.evaluation.iteration;

import networks.evaluation.values.Value;
import networks.structure.metadata.NeuronMapping;

import java.util.logging.Logger;

/**
 * An aglomeration of special classes for storing computational and structural States (see interface State) of Neurons.
 */
public class States implements State {
    private static final Logger LOG = Logger.getLogger(States.class.getName());

    /**
     * Storing a State that consists of a pair of values (value & gradient)
     */
    public static final class ComputationPair implements State.Computation.Pair {
        Value value;
        Value gradient;

        @Override
        public final Value getValue() {
            return value;
        }

        @Override
        public final Value getGradient() {
            return gradient;
        }
    }

    /**
     * Storing a State that is an array of other States, e.g. for parallel mini-batch processing, where different views of the same neuron may operate with different states over that single neuron.
     *
     * @param <T>
     */
    public static final class ComputationArray<T extends State.Computation> implements State.Computation {
        public final T[] states;

        public ComputationArray(T[] states) {
            this.states = states;
        }
    }

    /**
     * Storing inputs and outputs of each neuron (may vary due to neuron sharing in different contexts)
     */
    public static final class StructurePair implements State.Structure.Pair {
        NeuronMapping inputs;
        NeuronMapping outputs;

        @Override
        public final NeuronMapping getInputs() {
            return inputs;
        }

        @Override
        public final NeuronMapping getOutputs() {
            return outputs;
        }
    }

    /**
     * Simple storage of parent count for efficient backprop computation (may vary due to neuron sharing in different contexts)
     */
    public static final class StructureParents implements State.Structure {
        public final int count;
        public int checked = 0;

        public StructureParents(int count) {
            this.count = count;
        }
    }
}