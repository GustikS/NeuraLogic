package networks.structure;

import ida.utils.tuples.Pair;
import learning.Example;
import networks.structure.lrnnTypes.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by gusta on 8.3.17.
 *
 * //todo after creation and post-processing, add a transformation to a more optimized version (everything based on int, maybe even precompute layers, remove recursion)
 */
public class NeuralNetwork implements Example {
    String id;

    Neurons neurons;    //todo heavy object, cosider removing after calculation (push this out in a separate NeuralNet classes with different optimizations)

    /**
     * Locally valid input overloading for some neurons to facilitate dynamic structure changes
     */
    public @Nullable Map<Neuron, ArrayList<Pair<Neuron, Weight>>> extraInputMapping;

    public class Neurons {
        /**
         * All neurons combined in TOPOLOGICAL ORDERING. <- todo
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
        this.neurons.allNeuronsTopologic = new ArrayList<>(atomNeurons.size() + aggregationNeurons.size() + ruleNeurons.size() + factNeurons.size() + negationNeurons.size());

        this.neurons.atomNeurons.addAll(atomNeurons);
        this.neurons.aggNeurons.addAll(aggregationNeurons);
        this.neurons.ruleNeurons.addAll(ruleNeurons);
        this.neurons.factNeurons.addAll(factNeurons);
        this.neurons.negationNeurons.addAll(negationNeurons);

        this.neurons.allNeuronsTopologic.addAll(atomNeurons);
        this.neurons.allNeuronsTopologic.addAll(aggregationNeurons);
        this.neurons.allNeuronsTopologic.addAll(ruleNeurons);
        this.neurons.allNeuronsTopologic.addAll(factNeurons);
        this.neurons.allNeuronsTopologic.addAll(negationNeurons);
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
}