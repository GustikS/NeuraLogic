package networks.structure.components;

import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.types.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NeuronSets {

    public List<AtomNeuron> atomNeurons;
    public List<WeightedAtomNeuron> weightedAtomNeurons;
    public List<AggregationNeuron> aggNeurons;
    public List<RuleNeuron> ruleNeurons;
    public List<WeightedRuleNeuron> weightedRuleNeurons;
    public List<FactNeuron> factNeurons;
    public List<NegationNeuron> negationNeurons;

    List<BaseNeuron> roots;
    List<BaseNeuron> leaves;

    public NeuronSets() {
        this.atomNeurons = new ArrayList<>();
        this.weightedAtomNeurons = new ArrayList<>();
        this.aggNeurons = new ArrayList<>();
        this.ruleNeurons = new ArrayList<>();
        this.weightedRuleNeurons = new ArrayList<>();
        this.factNeurons = new ArrayList<>();
        this.negationNeurons = new ArrayList<>();
    }

    public NeuronSets(Collection<AtomNeurons> atomNeurons, Collection<AggregationNeuron> aggregationNeurons, Collection<RuleNeurons> ruleNeurons, Collection<FactNeuron> factNeurons, Collection<NegationNeuron> negationNeurons) {

        this.atomNeurons = new ArrayList<>();
        this.weightedAtomNeurons = new ArrayList<>();
        for (AtomNeurons atomNeuron : atomNeurons) {
            if (atomNeuron instanceof WeightedAtomNeuron) {
                this.weightedAtomNeurons.add((WeightedAtomNeuron) atomNeuron);
            } else {
                this.atomNeurons.add((AtomNeuron) atomNeuron);
            }
        }
        this.aggNeurons = new ArrayList<>(aggregationNeurons);
        this.ruleNeurons = new ArrayList<>();
        this.weightedRuleNeurons = new ArrayList<>();
        for (RuleNeurons ruleNeuron : ruleNeurons) {
            if (ruleNeuron instanceof WeightedRuleNeuron) {
                this.weightedRuleNeurons.add((WeightedRuleNeuron) ruleNeuron);
            } else {
                this.ruleNeurons.add((RuleNeuron) ruleNeuron);
            }
        }
        this.factNeurons = new ArrayList<>(factNeurons);
        this.negationNeurons = new ArrayList<>(negationNeurons);
    }

    public List<Neurons> getAllNeurons() {
        List<Neurons> allNeurons = new ArrayList<>();
        allNeurons.addAll(atomNeurons);
        allNeurons.addAll(weightedAtomNeurons);
        allNeurons.addAll(ruleNeurons);
        allNeurons.addAll(weightedRuleNeurons);
        allNeurons.addAll(aggNeurons);
        allNeurons.addAll(factNeurons);
        allNeurons.addAll(negationNeurons);
        return allNeurons;
    }

    public NeuronCounter getCounts() {
        return new NeuronCounter(this);
    }

    public class NeuronCounter {

        int atomNeuronsCreated;
        int weightedAtomNeuronsCreated;
        int aggNeuronsCreated;
        int ruleNeuronsCreated;
        int weightedRuleNeuronsCreated;
        int factNeuronsCreated;

        public NeuronCounter(NeuronSets neuronSets) {
            atomNeuronsCreated = neuronSets.atomNeurons.size();
            weightedAtomNeuronsCreated = neuronSets.weightedAtomNeurons.size();
            aggNeuronsCreated = neuronSets.aggNeurons.size();
            ruleNeuronsCreated = neuronSets.ruleNeurons.size();
            weightedRuleNeuronsCreated = neuronSets.weightedRuleNeurons.size();
            factNeuronsCreated = neuronSets.factNeurons.size();
        }

    }
}
