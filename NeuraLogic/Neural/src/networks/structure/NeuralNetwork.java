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
    /**
     * All neurons combined.
     */
    List<Neuron> neurons;

    List<AtomNeuron> atomNeurons;
    List<AggregationNeuron> aggNeurons;
    List<RuleNeuron> ruleNeurons;
    List<FactNeuron> factNeurons;
    List<NegationNeuron> negationNeurons;

    public NeuralNetwork(Collection<AtomNeuron> atomNeurons, Collection<AggregationNeuron> aggregationNeurons, Collection<RuleNeuron> ruleNeurons, Set<FactNeuron> factNeurons, Set<NegationNeuron> negationNeurons) {
        this.neurons = new ArrayList<>(atomNeurons.size() + aggregationNeurons.size() + ruleNeurons.size() + factNeurons.size() + negationNeurons.size());

        this.atomNeurons = new ArrayList<>(atomNeurons);
        this.aggNeurons = new ArrayList<>(aggregationNeurons);
        this.ruleNeurons = new ArrayList<>(ruleNeurons);
        this.factNeurons = new ArrayList<>(factNeurons);
        this.negationNeurons = new ArrayList<>(negationNeurons);

        neurons.addAll(this.atomNeurons);
        neurons.addAll(this.aggNeurons);
        neurons.addAll(this.ruleNeurons);
        neurons.addAll(this.factNeurons);
        neurons.addAll(this.negationNeurons);
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
        return neurons.size();
    }

    public void setId(String id) {
        this.id = id;
    }
}