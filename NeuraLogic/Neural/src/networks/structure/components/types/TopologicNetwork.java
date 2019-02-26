package networks.structure.components.types;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;
import utils.generic.Pair;

import java.util.*;
import java.util.logging.Logger;

/**
 * A class with an explicitly stored (topological) ordering of neurons.
 */
public class TopologicNetwork<N extends State.Neural.Structure> extends NeuralNetwork<N> {
    private static final Logger LOG = Logger.getLogger(TopologicNetwork.class.getName());

    /**
     * All neurons combined in TOPOLOGICAL ORDERING.
     */
    public List<BaseNeuron<BaseNeuron, State.Neural>> allNeuronsTopologic;

    public TopologicNetwork(String id, List<BaseNeuron<BaseNeuron, State.Neural>> allNeurons){
        super(id, allNeurons.size());
        allNeuronsTopologic = topologicSort(allNeurons);
    }

    public TopologicNetwork(String id, int size) {
        super(id, size);
        allNeuronsTopologic = new ArrayList<>(size);
    }

    public TopologicNetwork(String id, List<BaseNeuron<BaseNeuron, State.Neural>> allNeurons, boolean sorted){
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
    public List<BaseNeuron<BaseNeuron, State.Neural>> topologicSort(List<BaseNeuron<BaseNeuron, State.Neural>> allNeurons) {
        Set<BaseNeuron> visited = new HashSet<>();
        Stack<BaseNeuron> stack = new Stack<>();

        for (BaseNeuron neuron : allNeurons) {
            if (!visited.contains(neuron))
                topoSortRecursive(neuron, visited, stack);
        }

        List<BaseNeuron<BaseNeuron, State.Neural>> neurons = new ArrayList<>(allNeurons.size());
        while (!stack.empty())
            neurons.add(stack.pop());

        return neurons;
    }

    private void topoSortRecursive(BaseNeuron neuron, Set<BaseNeuron> visited, Stack<BaseNeuron> stack) {
        visited.add(neuron);

        Iterator<Pair<BaseNeuron, Weight>> inputs = getInputs(neuron);
        while (inputs.hasNext()) {
            Pair<BaseNeuron, Weight> next = inputs.next();
            if (!visited.contains(next.r)) {
                topoSortRecursive(next.r, visited, stack);
            }
        }
        stack.push(neuron);
    }
}