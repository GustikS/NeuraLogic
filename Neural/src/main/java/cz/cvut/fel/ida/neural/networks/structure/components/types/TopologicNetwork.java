package cz.cvut.fel.ida.neural.networks.structure.components.types;

import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class with an explicitly stored (topological) ordering of neurons.
 */
public class TopologicNetwork<N extends State.Neural.Structure> extends NeuralNetwork<N> {
    private static final Logger LOG = Logger.getLogger(TopologicNetwork.class.getName());

    /**
     * All neurons combined in TOPOLOGICAL ORDERING.
     */
    public List<BaseNeuron<Neurons, State.Neural>> allNeuronsTopologic;

    public TopologicNetwork(String id, List<BaseNeuron<Neurons, State.Neural>> allNeurons) {
        super(id, allNeurons.size());
        allNeuronsTopologic = topologicSort(allNeurons);

        if (allNeuronsTopologic.size() != allNeurons.size()) {
            LOG.warning("Some neurons connected in the network are not in neuronmaps!");
        }

        if (LOG.isLoggable(Level.FINEST))
            LOG.finest(allNeuronsTopologic.toString());
    }

    public TopologicNetwork(String id, int size) {
        super(id, size);
        allNeuronsTopologic = new ArrayList<>(size);
    }

    public TopologicNetwork(String id, List<BaseNeuron<Neurons, State.Neural>> allNeurons, boolean sorted) {
        super(id, allNeurons.size());
        allNeuronsTopologic = allNeurons;
    }

    public TopologicNetwork(List<AtomNeurons> queryNeurons, String id) {
        super(id, -1);
        Set<Neurons> visited = new HashSet<>();
        LinkedList<BaseNeuron<Neurons, State.Neural>> stack = new LinkedList<>();
        for (AtomNeurons queryNeuron : queryNeurons) {
            if (visited.contains(queryNeuron)){
                continue;
            }
            BaseNeuron<Neurons, State.Neural> outputStart1 = (BaseNeuron<Neurons, State.Neural>) queryNeuron;
            topoSortRecursive(outputStart1, visited, stack);
        }
        List<BaseNeuron<Neurons, State.Neural>> reverse = new ArrayList<>(stack.size());
        Iterator<BaseNeuron<Neurons, State.Neural>> descendingIterator = stack.descendingIterator();
        descendingIterator.forEachRemaining(reverse::add);
        this.allNeuronsTopologic = reverse;
        this.neuronCount = allNeuronsTopologic.size();
    }

    /**
     * Reorder indices of the neurons assigned (incrementaly/randomly) during creation by NeuronFactory to respect the topologic order
     */
    public void sortIndices() {
        int[] indices = new int[allNeuronsTopologic.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = allNeuronsTopologic.get(i).index;
        }
        Arrays.sort(indices);
        int prev = -1;
        for (int i = 0; i < allNeuronsTopologic.size(); i++) {
            allNeuronsTopologic.get(i).index = indices[i];
            if (indices[i] == prev){
                LOG.severe("Duplicit neuron indices detected!!");
            }
            prev = indices[i];
        }
    }

    /**
     * Restart indices from 0 - we change the neuron indices here, so that they are not unique anymore! (only within each network)
     */
    public void restartIndices() {
        for (int i = 0; i < allNeuronsTopologic.size(); i++) {
            allNeuronsTopologic.get(i).index = i;
        }
    }

    @Deprecated
    public N getState(int index) {
        return neuronStates.getState(index);
    }

    /**
     * MAKE SURE the neuron index is properly changed to point to cache before calling!!
     *
     * @param neuron
     * @return
     */
    @Override
    public N getState(Neurons neuron) {
        if (neuronStates != null) {
            return neuronStates.getState(neuron.getIndex());
        }
        return null;
    }

    /**
     * Sort all the neuron for topologic ordering, irrespective of what is the output.
     * <p>
     * This is different to using existing DFS iterators in bottom-up mode, which also return topologically ordered neurons, since we are not given a QueryNeuron to start with.
     * - hence we need the hashset for checking whether we have already visited each neuron.
     * - this could be possibly emulated through a stateVisitor marking the states of neurons as visited
     * - but that would force some manipulation with the actual states of neurons, which is not desirable here
     * - we would have to undo that in another pass, making it possibly even more computationally demanding
     *
     * @param allNeurons
     * @return
     */
    public List<BaseNeuron<Neurons, State.Neural>> topologicSort(List<BaseNeuron<Neurons, State.Neural>> allNeurons) {
        Set<Neurons> visited = new HashSet<>();
        LinkedList<BaseNeuron<Neurons, State.Neural>> stack = new LinkedList<>();

        for (BaseNeuron<Neurons, State.Neural> neuron : allNeurons) {
            if (!visited.contains(neuron))
                topoSortRecursive(neuron, visited, stack);
        }
        //Collections.reverse(stack);
        List<BaseNeuron<Neurons, State.Neural>> reverse = new ArrayList<>(stack.size());
        Iterator<BaseNeuron<Neurons, State.Neural>> descendingIterator = stack.descendingIterator();
        while (descendingIterator.hasNext()) {
            reverse.add(descendingIterator.next());
        }
        return reverse;
    }

    public void topoSortRecursive(BaseNeuron<Neurons, State.Neural> neuron, Set<Neurons> visited, LinkedList<BaseNeuron<Neurons, State.Neural>> stack) {
        visited.add(neuron);

        Iterator<Neurons> inputs = getInputs(neuron);
        while (inputs.hasNext()) {
            Neurons next = inputs.next();
            if (!visited.contains(next)) {
                topoSortRecursive((BaseNeuron<Neurons, State.Neural>) next, visited, stack);
            }
        }
        stack.addFirst(neuron);
    }
    @Override
    public String toString() {
        return "net:" + id + ", neurons: " + allNeuronsTopologic.size();
    }
}