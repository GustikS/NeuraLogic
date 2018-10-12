package networks.structure.metadata.states;

import networks.structure.neurons.Neuron;

import java.util.HashMap;
import java.util.logging.Logger;

public abstract class NeuronStates<T extends State.Structure> {
    private static final Logger LOG = Logger.getLogger(NeuronStates.class.getName());
    /**
     * For topologicNetwork must be aligned (ordered the same as) the topologic neurons list - that saves one findNeuron() call.
     * For other networks without implicit neurons ordering, index of a neuron must be found first with findNeuron() against sortedNeuronIndices.
     * <p>
     * Depending on different settings of neuron sharing, Evaluation, Backprop and Parallelization, the network context needs to store information that changes from network to network.
     * For each neuron, these are all/either of inputs, outputs and number of parents. There is no need to store computation state at the level of network, as that belongs to individual neurons.
     */
    T[] neuronStates;

    public NeuronStates(T[] neuronStates){
        this.neuronStates = neuronStates;
    }

    protected abstract int findNeuron(int idx);

    T getState(Neuron neuron) {
        int idx = findNeuron(neuron.index);
        if (idx < 0 || idx > neuronStates.length) {
            LOG.severe("ERROR - out of bounds access to getState of a neuron: " + neuron);
            return null;
        }
        return neuronStates[idx];
    }

    public static class LinearCache extends NeuronStates {
        private int[] neuronIndices;

        public LinearCache(State.Structure[] neuronStates) {
            super(neuronStates);
        }

        public int findNeuron(int index) {
            for (int i = 0; i < neuronIndices.length; i++) {
                if (neuronIndices[i] == index)
                    return i;
            }
            return -1;
        }
    }

    public static class HeapCache extends NeuronStates {
        /**
         * A stored mapping of all contained neurons to internal index (for a more efficient storing and access to neurons, if needed, than holding them within na HashMap)
         */
        private int[] heapSortedNeuronIndices;

        public HeapCache(State.Structure[] neuronStates) {
            super(neuronStates);
        }

        public int findNeuron(int target) {
            int index = 0;
            while (index < heapSortedNeuronIndices.length)
                if (target >= heapSortedNeuronIndices[index]) {
                    index <<= 2;
                } else if (target < heapSortedNeuronIndices[index]) {
                    index <<= 2 + 1;
                } else {
                    return index;
                }
            return -1;
        }
    }

    public static class HashCache extends NeuronStates {
        /**
         * The same mapping as above, but using a hashmap - may be faster for very big networks.
         */
        public HashMap<Integer, Integer> neuronIndices;

        public HashCache(State.Structure[] neuronStates) {
            super(neuronStates);
        }

        @Override
        protected int findNeuron(int idx) {
            return neuronIndices.get(idx);
        }
    }
}
