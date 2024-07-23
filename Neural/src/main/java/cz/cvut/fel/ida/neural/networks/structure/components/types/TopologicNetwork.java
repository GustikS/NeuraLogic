package cz.cvut.fel.ida.neural.networks.structure.components.types;

import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;
import cz.cvut.fel.ida.utils.generic.Pair;

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
        allNeuronsTopologic = new TopoSorting().topologicSort(allNeurons);

        if (allNeuronsTopologic.size() != allNeurons.size()) {
            LOG.warning("Some neurons connected in the network are not in neuronmaps!");
        }

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(allNeuronsTopologic.toString());
        }
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
        this.allNeuronsTopologic = new TopoSorting().topologicSort(queryNeurons);
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
            if (indices[i] == prev) {
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

    @Override
    public String toString() {
        return "net:" + id + ", neurons: " + allNeuronsTopologic.size();
    }

    /**
     * A stateful object for sorting all the neurons for topologic ordering, given some starting (query) nodes, but these can possibly be even the full set of all nodes
     * <p>
     * Note that this is a bit different to using existing DFS iterators in bottom-up mode, which also return topologically ordered neurons, since we are not given a single QueryNeuron to start with.
     * - hence we need a hashset for checking whether we have already visited each neuron.
     *  - this hashset is replaced now with marking the neurons as OPEN/CLOSED respectively
     *
     */
    public class TopoSorting {

        LinkedList<Neurons> topoList = new LinkedList<>();

        static final int OPEN = 1;
        static final int CLOSED = -1;
        static final int DEFAULT = 0;

        public List<BaseNeuron<Neurons, State.Neural>> topologicSort(List<? extends Neurons> startNeurons) {

            for (Neurons neuron : startNeurons) {
                if (neuron == null){
                    continue;   // we can ignore missing output query neurons - they correspond to unmatched samples
                }
                if (neuron.getLayer() == DEFAULT) {  // it should never be OPEN here...
                    topoSortRecursive(neuron);
                }
            }

            //Collections.reverse(stack);
            List<BaseNeuron<Neurons, State.Neural>> reverse = new ArrayList<>(topoList.size());
            Iterator<Neurons> descendingIterator = topoList.descendingIterator();
            while (descendingIterator.hasNext()) {
                BaseNeuron<Neurons, State.Neural> next = (BaseNeuron<Neurons, State.Neural>) descendingIterator.next();
                next.setLayer(DEFAULT);
                reverse.add(next);
            }
            return reverse;
        }

        private void topoSortRecursive(Neurons neuron) {
            neuron.setLayer(OPEN);

            Iterator<Neurons> inputs = getInputs(neuron);
            while (inputs.hasNext()) {
                Neurons input = inputs.next();
                if (input.getLayer() != OPEN) {
                    if (input.getLayer() != CLOSED) {
                        topoSortRecursive(input);
                    }
                } else {
                    removeBackEdge(neuron, inputs, input);  // recursive edge!
                    LOG.warning("Cycle detected in ground neural network. A backEdge: " + neuron + " --> " + input + " has been removed!");
                }
            }

            neuron.setLayer(CLOSED);
            topoList.addFirst(neuron);
        }

        private void removeBackEdge(Neurons neuron, Iterator<Neurons> inputs, Neurons input) {
            if (neuron instanceof WeightedNeuron){
                Pair<Iterator<Neurons>, Iterator<Weight>> weightedInputs = getInputs((WeightedNeuron<Neurons, State.Neural>) neuron);
                Iterator<Neurons> inputNeurons = weightedInputs.r;
                Iterator<Weight> inputWeights = weightedInputs.s;
                while (inputNeurons.hasNext()){
                    inputWeights.next();
                    if (inputNeurons.next() == input){
                        inputWeights.remove();
                        break;  // removing single edge at a time (the first one w.r.t. the outer iteration)
                    }
                }
            }
            inputs.remove();
        }
    }
}