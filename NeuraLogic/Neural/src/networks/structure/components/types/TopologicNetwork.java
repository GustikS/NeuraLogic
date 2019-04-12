package networks.structure.components.types;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;

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
    public List<BaseNeuron<Neuron, State.Neural>> allNeuronsTopologic;

    public TopologicNetwork(String id, List<BaseNeuron<Neuron, State.Neural>> allNeurons) {
        super(id, allNeurons.size());
        allNeuronsTopologic = topologicSort(allNeurons);

        if (allNeuronsTopologic.size() != allNeurons.size()){
            LOG.warning("Some neurons connected in the network are not in neuronmaps!");
        }

        if (LOG.isLoggable(Level.FINEST))
            LOG.finest(allNeuronsTopologic.toString());
    }

    public TopologicNetwork(String id, int size) {
        super(id, size);
        allNeuronsTopologic = new ArrayList<>(size);
    }

    public TopologicNetwork(String id, List<BaseNeuron<Neuron, State.Neural>> allNeurons, boolean sorted) {
        super(id, allNeurons.size());
        allNeuronsTopologic = allNeurons;
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
    public N getState(BaseNeuron neuron) {
        if (neuronStates != null) {
            return neuronStates.getState(neuron.index);
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
    public List<BaseNeuron<Neuron, State.Neural>> topologicSort(List<BaseNeuron<Neuron, State.Neural>> allNeurons) {
        Set<Neuron> visited = new HashSet<>();
        LinkedList<BaseNeuron<Neuron, State.Neural>> stack = new LinkedList<>();

        for (BaseNeuron<Neuron, State.Neural> neuron : allNeurons) {
            if (!visited.contains(neuron))
                topoSortRecursive(neuron, visited, stack);
        }
        //Collections.reverse(stack);
        List<BaseNeuron<Neuron, State.Neural>> reverse = new ArrayList<>(stack.size());
        Iterator<BaseNeuron<Neuron, State.Neural>> descendingIterator = stack.descendingIterator();
        while (descendingIterator.hasNext()){
            reverse.add(descendingIterator.next());
        }
        return reverse;
    }

    private void topoSortRecursive(BaseNeuron<Neuron, State.Neural> neuron, Set<Neuron> visited, LinkedList<BaseNeuron<Neuron, State.Neural>> stack) {
        visited.add(neuron);

        Iterator<Neuron> inputs = getInputs(neuron);
        while (inputs.hasNext()) {
            Neuron next = inputs.next();
            if (!visited.contains(next)) {
                topoSortRecursive((BaseNeuron<Neuron, State.Neural>) next, visited, stack);
            }
        }
        stack.addFirst(neuron);
    }
}