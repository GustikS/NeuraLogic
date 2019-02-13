package networks.structure.metadata.states;

import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.neurons.BaseNeuron;

import java.util.HashMap;
import java.util.logging.Logger;

public abstract class StatesCache<T extends State.Neural.Structure> {
    private static final Logger LOG = Logger.getLogger(StatesCache.class.getName());
    /**
     * Stores a State <T> for each Neuron in this state cache (corresponding to one neural net). Neurons are translated to an index to this array with findNeuron().
     *
     * For topologicNetwork this array must be aligned (ordered the same as) the topologic neurons list - that saves one findNeuron() call.
     * For other networks without implicit neurons ordering, index of a neuron must be found first with findNeuron() against respective neuronIndices storage.
     * <p>
     * Depending on different settings of neuron sharing, Evaluation, Backprop and Parallelization, the network context needs to store information that changes for individual neurons from network to network.
     * For each neuron, these are all/either of inputs, outputs and number of parents. There is no need to store computation state at the level of network, as that belongs to individual neurons.
     * Should a computation state of a neuron change with context of a network, e.g. with parallel access to it, then that is handled with an {@link networks.structure.metadata.states.States.ComputationStateComposite} and indexing of the access.
     * This is different since that information (e.g. value or gradient) is only temporary (and need to have fast access) and needs to be calculated over and over,
     * while the Structure States (e.g. true inputs/outputs) of a Neuron are permanently stored in the context of a network (calculated once).
     */
    T[] neuronStates;

    /**
     * This visitor will process all the <T> states stored in this cache. Good for transfer of stored information to Computation states before computation processing
     */
    StateVisiting.Computation initializer;

    public StatesCache(T[] neuronStates, StateVisiting.Computation initializer){
        this.neuronStates = neuronStates;
        this.initializer = initializer;
    }

    protected abstract int findNeuron(int idx);

    public T getState(int index){
        return neuronStates[index];
    }

    public T getState(BaseNeuron neuron) {
        int idx = findNeuron(neuron.index);
        if (idx < 0 || idx > neuronStates.length) {
            LOG.severe("ERROR - out of bounds access to getState of a neuron: " + neuron);
            return null;
        }
        return neuronStates[idx];
    }

    /**
     * This is not an invalidation but rather a transfer of information from Structure states to Computation states
     */
    public void initialize(int stateView) {
        initializer.stateIndex = stateView;
        for (T neuronState : neuronStates) {
            neuronState.accept(initializer);
        }
    }

    public static class LinearCache extends StatesCache {
        /**
         * Simple storage of neuron indices in an (unsorted) array of them. Fast linear search for small arrays (networks).
         */
        private int[] neuronIndices;

        public LinearCache(State.Neural.Structure[] neuronStates, StateVisiting.Computation initializer) {
            super(neuronStates, initializer);
        }

        public int findNeuron(int index) {
            for (int i = 0; i < neuronIndices.length; i++) {
                if (neuronIndices[i] == index)
                    return i;
            }
            return -1;
        }
    }

    public static class HeapCache extends StatesCache {
        /**
         * A heap sorted array of indices of neurons. Fast BST search for medium sized arrays (networks).
         */
        private int[] heapSortedNeuronIndices;

        public HeapCache(State.Neural.Structure[] neuronStates, StateVisiting.Computation initializer) {
            super(neuronStates, initializer);
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

    public static class HashCache extends StatesCache {
        /**
         * The same mapping as above, but using a hashmap - faster for very big mappings (networks).
         */
        public HashMap<Integer, Integer> neuronIndices;

        public HashCache(State.Neural.Structure[] neuronStates, StateVisiting.Computation initializer) {
            super(neuronStates, initializer);
        }

        @Override
        protected int findNeuron(int idx) {
            return neuronIndices.get(idx);
        }
    }
}
