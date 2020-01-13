package networks.structure.components.types;

import com.sun.istack.internal.Nullable;
import networks.structure.components.NeuronSets;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.NetworkMetadata;
import networks.structure.metadata.inputMappings.LinkedMapping;
import networks.structure.metadata.inputMappings.NeuronMapping;
import networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import networks.structure.metadata.states.State;
import utils.generic.Pair;

import java.util.*;
import java.util.logging.Logger;

/**
 * A specific {@link networks.structure.components.NeuralNetwork} type, meant for storing all the extra information accumulated during neural net creation,
 * which might be useful for various neural nets postprocessing steps. It provides the basic neural net functionality as well, but is not meant for regular use.
 * <p>
 * When the postprocessing is done, most of the information is discarded and an optimized version for both memory and speed is created, e.g. {@link TopologicNetwork}.
 *
 * @param <N>
 */
public class DetailedNetwork<N extends State.Neural.Structure> extends TopologicNetwork<N> {
    private static final Logger LOG = Logger.getLogger(DetailedNetwork.class.getName());

    /**
     * Locally valid input overloading for some neurons to facilitate dynamic structure changes.
     * This map is only to be used before the faster neural {@link networks.structure.metadata.states.StatesCache} is created for the same thing in a regular network.
     */
    @Nullable
    public Map<BaseNeuron, NeuronMapping<Neurons>> extraInputMapping;

    /**
     * Outputs of neurons, are only rarely used (not stored in a regular neural net to save space)
     */
    @Nullable
    public Map<BaseNeuron, NeuronMapping<Neurons>> outputMapping;

    @Nullable
    public NeuronSets allNeurons;

    /**
     * Cumulating all the states that will be necessary for each of the neurons before the final state objects are created to go into fast cache
     */
    public Map<Neurons, List<State.Neural.Structure>> cumulativeStates;

    /**
     * Number of shared neuron only AT THE TIME OF CREATION, i.e. if later some of them become shared, this needs to be recounted.
     */
    public int sharedNeuronsCount;

    public DetailedNetwork(String id, List<BaseNeuron<Neurons, State.Neural>> allNeurons) {
        super(id, allNeurons);
        cumulativeStates = new LinkedHashMap<>();
    }

    public DetailedNetwork(String id, int size) {
        super(id, size);
        cumulativeStates = new LinkedHashMap<>();
    }

    Boolean recursive;

    @Nullable
    NetworkMetadata metadata;

    public DetailedNetwork(String id, List<BaseNeuron<Neurons, State.Neural>> allNeurons, NeuronSets neurons) {
        this(id, allNeurons);
        this.allNeurons = neurons;
    }

    public List<Weight> getAllWeights() {
        Set<Weight> allWeights = new HashSet<>();
        for (BaseNeuron<Neurons, State.Neural> neuron : allNeuronsTopologic) {
            if (neuron instanceof WeightedNeuron) {
                WeightedNeuron weightedNeuron = (WeightedNeuron) neuron;
                allWeights.addAll(weightedNeuron.getWeights());
                allWeights.add(weightedNeuron.getOffset());
            }
        }
        return new ArrayList<>(allWeights);
    }

    public <T extends Neurons, S extends State.Neural> Pair<Iterator<T>, Iterator<Weight>> getInputs(WeightedNeuron<T, S> neuron) {
        WeightedNeuronMapping<T> inputMapping;
        if ((inputMapping = extraInputMapping != null ? (WeightedNeuronMapping<T>) extraInputMapping.get(neuron) : null) != null) {
            Iterator<T> iterator = inputMapping.iterator();
            Iterator<Weight> weightIterator = inputMapping.weightIterator();
            return new Pair<>(iterator, weightIterator);
        } else {
            return super.getInputs(neuron);
        }
    }

    //todo move getInputs methods into neurons? = avoid casting, as the neuron knows its type
    public <T extends Neurons, S extends State.Neural> Iterator<T> getInputs(BaseNeuron<T, S> neuron) {
        NeuronMapping<T> inputMapping;
        if ((inputMapping = extraInputMapping != null ? (NeuronMapping<T>) extraInputMapping.get(neuron) : null) != null) {
            return inputMapping.iterator();
        } else {
            return neuron.getInputs().iterator();
        }
    }

    public <T extends Neurons, S extends State.Neural> Iterator<Neurons> getOutputs(BaseNeuron<T, S> neuron) {
        LinkedMapping<Neurons> mapping;
        if ((mapping = outputMapping != null ? outputMapping.get(neuron) : null) != null) {
            return mapping.iterator();
        } else {
            return null;
        }
    }

    public void addState(Neurons neuron, State.Structure state) {
        List<State.Structure> states = cumulativeStates.putIfAbsent(neuron, new LinkedList<>());
        states.add(state);
    }

    public boolean isRecursive() {
        return recursive;
    }


    public void setSharedNeuronsCount(int sharedNeuronsCount) {
        this.hasSharedNeurons = sharedNeuronsCount > 0;
        this.sharedNeuronsCount = sharedNeuronsCount;
    }

    /**
     * Replace input neuron with another neuron (used e.g. in linear chain pruning)
     *
     * @param parentNeuron
     * @param toReplace
     * @param replaceWith
     */
    public void replaceInput(BaseNeuron<Neurons, State.Neural> parentNeuron, Neurons toReplace, Neurons replaceWith) {
        NeuronMapping<Neurons> inputMapping;
        if ((inputMapping = extraInputMapping != null ? (NeuronMapping<Neurons>) extraInputMapping.get(parentNeuron) : null) != null) {
            inputMapping.replace(toReplace, replaceWith);
        } else {
            for (int i = 0; i < parentNeuron.getInputs().size(); i++) {
                if (parentNeuron.getInputs().get(i).equals(toReplace)) {
                    parentNeuron.getInputs().set(i, replaceWith);
//                    break;
                }
            }
        }
    }

    public void replaceOutput(BaseNeuron<Neurons, State.Neural> child, Neurons middle, Neurons parent) {
        NeuronMapping<Neurons> outputs;
        if (outputMapping == null || (outputs = outputMapping.get(child)) == null) {
            LOG.severe("OutputMapping requested but missing!");
            return;
        }
        outputs.replace(middle, parent);
    }

    public <S extends State.Neural, T extends Neurons> void replaceInputWeight(WeightedNeuron<T, S> parentNeuron, T toReplace, Weight finalWeight) {
        WeightedNeuronMapping<T> inputMapping;
        if ((inputMapping = extraInputMapping != null ? (WeightedNeuronMapping<T>) extraInputMapping.get(parentNeuron) : null) != null) {   //todo test this
            Iterator<T> iterator = inputMapping.iterator();
            WeightedNeuronMapping.WeightIterator weightIterator = (WeightedNeuronMapping.WeightIterator) inputMapping.weightIterator();
            while (iterator.hasNext()) {
                T next = iterator.next();
                Weight nextW = weightIterator.next();
                if (next.equals(toReplace)) {
                    weightIterator.replace(finalWeight);
                }
            }
        } else {
            for (int i = 0; i < parentNeuron.getInputs().size(); i++) {
                if (parentNeuron.getInputs().get(i).equals(toReplace)) {
                    parentNeuron.getWeights().set(i, finalWeight);
                }
            }
        }
    }
}