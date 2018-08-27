package networks.structure;

import learning.Example;
import networks.structure.lrnnTypes.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralNetwork implements Example{
    String id;
    List<Neuron> neurons;

    public NeuralNetwork(Collection<AtomNeuron> atomNeurons, Collection<AggregationNeuron> aggregationNeurons, Collection<RuleNeuron> ruleNeurons, Set<FactNeuron> factNeurons, Set<NegationNeuron> negationNeurons) {
        //todo next
    }

    boolean isRecursive(){
        return false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getSize() {
        return null;
    }

    public void setId(String id) {
        this.id = id;
    }
}