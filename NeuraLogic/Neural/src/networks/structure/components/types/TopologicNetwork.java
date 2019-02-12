package networks.structure.components.types;

import ida.utils.tuples.Pair;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

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
    public List<Neuron<Neuron, State.Neural>> allNeuronsTopologic;

    public TopologicNetwork(String id, int size) {
        super(id, size);
        allNeuronsTopologic = new ArrayList<>(size);
    }

    @Override
    public Integer getSize() {
        return allNeuronsTopologic.size();
    }

    public N getState(int index) {
        return neuronStates.getState(index);
    }

    /**
     * Overwriting the neuron index to point to cache must be done before calling
     * @param neuron
     * @return
     */
    @Override
    public N getState(Neuron neuron){
        if (neuronStates != null) {
            return neuronStates.getState(neuron.index);
        }
        return null;
    }

    /**
     * todo replace with a more efficient version without hashset (using existing DFS iterators)
     * @param allNeurons
     * @return
     */
    public List<Neuron> topologicSort(List<Neuron> allNeurons) {
        Set<Neuron> visited = new HashSet<>();
        Stack<Neuron> stack = new Stack<>();

        for (Neuron neuron : allNeurons) {
            if (!visited.contains(neuron))
                topoSortRecursive(neuron, visited, stack);
        }

        List<Neuron> neurons = new ArrayList<>(allNeurons.size());
        while (!stack.empty())
            neurons.add(stack.pop());

        return neurons;
    }

    private void topoSortRecursive(Neuron neuron, Set<Neuron> visited, Stack<Neuron> stack) {
        visited.add(neuron);

        Iterator<Pair<Neuron, Weight>> inputs = getInputs(neuron);
        while (inputs.hasNext()) {
            Pair<Neuron, Weight> next = inputs.next();
            if (!visited.contains(next.r)) {
                topoSortRecursive(next.r, visited, stack);
            }
        }
        stack.push(neuron);
    }
}