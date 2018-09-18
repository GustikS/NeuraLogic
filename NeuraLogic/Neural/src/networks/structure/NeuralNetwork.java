package networks.structure;

import ida.utils.tuples.Pair;
import learning.Example;
import networks.structure.lrnnTypes.*;
import networks.structure.metadata.InputMapping;
import networks.structure.metadata.NetworkMetadata;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by gusta on 8.3.17.
 * <p>
 * //todo after creation and post-processing, add a transformation to a more optimized version (everything based on int, maybe even precompute layers, remove recursion)
 */
public class NeuralNetwork implements Example {
    String id;

    Neurons neurons;    //todo heavy object, consider removing after calculation (push this out in a separate NeuralNet classes with different optimizations)

    /**
     * Locally valid input overloading for some neurons to facilitate dynamic structure changes
     */
    public @Nullable Map<Neuron, InputMapping> extraInputMapping;

    @Nullable
    NetworkMetadata metadata;

    public class Neurons {
        /**
         * All neurons combined in TOPOLOGICAL ORDERING.
         */
        List<Neuron> allNeuronsTopologic;

        List<AtomNeuron> atomNeurons = new ArrayList<>();
        List<AggregationNeuron> aggNeurons = new ArrayList<>();
        List<RuleNeuron> ruleNeurons = new ArrayList<>();
        List<FactNeuron> factNeurons = new ArrayList<>();
        List<NegationNeuron> negationNeurons = new ArrayList<>();

        List<Neuron> roots;
        List<Neuron> leaves;
    }

    public NeuralNetwork(Collection<AtomNeuron> atomNeurons, Collection<AggregationNeuron> aggregationNeurons, Collection<RuleNeuron> ruleNeurons, Collection<FactNeuron> factNeurons, Set<NegationNeuron> negationNeurons) {
        this.neurons.atomNeurons.addAll(atomNeurons);
        this.neurons.aggNeurons.addAll(aggregationNeurons);
        this.neurons.ruleNeurons.addAll(ruleNeurons);
        this.neurons.factNeurons.addAll(factNeurons);
        this.neurons.negationNeurons.addAll(negationNeurons);

        List<Neuron> allNeurons = new ArrayList<>(atomNeurons.size() + aggregationNeurons.size() + ruleNeurons.size() + factNeurons.size() + negationNeurons.size());
        allNeurons.addAll(atomNeurons);
        allNeurons.addAll(aggregationNeurons);
        allNeurons.addAll(ruleNeurons);
        allNeurons.addAll(factNeurons);
        allNeurons.addAll(negationNeurons);
        this.neurons.allNeuronsTopologic = topologicSort(allNeurons);
    }

    private List<Neuron> topologicSort(List<Neuron> allNeurons) {
        //TODO next
    }

    boolean isRecursive() {
        return false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getSize() {
        return neurons.allNeuronsTopologic.size();
    }

    public void setId(String id) {
        this.id = id;
    }

    Iterator<Pair<Neuron, Weight>> inputsIterator(Neuron neuron) {
        InputMapping inputMapping;
        if ((inputMapping = extraInputMapping.get(neuron)) != null){
            //TODO NEXT
        }
    }
}