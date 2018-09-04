package networks.structure;

import learning.Example;
import networks.structure.lrnnTypes.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralNetwork implements Example {
    String id;

    Neurons neurons;

    public class Neurons {
        /**
         * All neurons combined.
         */
        List<Neuron> allNeurons;

        List<AtomNeuron> atomNeurons = new ArrayList<>();
        List<AggregationNeuron> aggNeurons = new ArrayList<>();
        List<RuleNeuron> ruleNeurons = new ArrayList<>();
        List<FactNeuron> factNeurons = new ArrayList<>();
        List<NegationNeuron> negationNeurons = new ArrayList<>();
    }

    public NeuralNetwork(Collection<AtomNeuron> atomNeurons, Collection<AggregationNeuron> aggregationNeurons, Collection<RuleNeuron> ruleNeurons, Collection<FactNeuron> factNeurons, Set<NegationNeuron> negationNeurons) {
        this.neurons.allNeurons = new ArrayList<>(atomNeurons.size() + aggregationNeurons.size() + ruleNeurons.size() + factNeurons.size() + negationNeurons.size());

        this.neurons.atomNeurons.addAll(atomNeurons);
        this.neurons.aggNeurons.addAll(aggregationNeurons);
        this.neurons.ruleNeurons.addAll(ruleNeurons);
        this.neurons.factNeurons.addAll(factNeurons);
        this.neurons.negationNeurons.addAll(negationNeurons);

        this.neurons.allNeurons.addAll(atomNeurons);
        this.neurons.allNeurons.addAll(aggregationNeurons);
        this.neurons.allNeurons.addAll(ruleNeurons);
        this.neurons.allNeurons.addAll(factNeurons);
        this.neurons.allNeurons.addAll(negationNeurons);
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
        return neurons.allNeurons.size();
    }

    public void setId(String id) {
        this.id = id;
    }
}