package cz.cvut.fel.ida.neural.networks.structure.components;

import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.*;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NeuralSets implements Exportable{

    public List<AtomNeuron> atomNeurons;
    public List<WeightedAtomNeuron> weightedAtomNeurons;
    public List<AggregationNeuron> aggNeurons;
    public List<RuleNeuron> ruleNeurons;
    public List<WeightedRuleNeuron> weightedRuleNeurons;
    public List<FactNeuron> factNeurons;
    public List<NegationNeuron> negationNeurons;

    List<BaseNeuron> roots;
    List<BaseNeuron> leaves;

    public NeuralSets() {
        this.atomNeurons = new ArrayList<>();
        this.weightedAtomNeurons = new ArrayList<>();
        this.aggNeurons = new ArrayList<>();
        this.ruleNeurons = new ArrayList<>();
        this.weightedRuleNeurons = new ArrayList<>();
        this.factNeurons = new ArrayList<>();
        this.negationNeurons = new ArrayList<>();
    }

    public NeuralSets(Collection<AtomNeurons> atomNeurons, Collection<AggregationNeuron> aggregationNeurons, Collection<RuleNeurons> ruleNeurons, Collection<FactNeuron> factNeurons, Collection<NegationNeuron> negationNeurons) {

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

    public class NeuronCounter implements Exportable {

        int atomNeuronsCreated;
        int weightedAtomNeuronsCreated;
        int aggNeuronsCreated;
        int ruleNeuronsCreated;
        int weightedRuleNeuronsCreated;
        int factNeuronsCreated;

        public NeuronCounter(NeuralSets neuralSets) {
            atomNeuronsCreated = neuralSets.atomNeurons.size();
            weightedAtomNeuronsCreated = neuralSets.weightedAtomNeurons.size();
            aggNeuronsCreated = neuralSets.aggNeurons.size();
            ruleNeuronsCreated = neuralSets.ruleNeurons.size();
            weightedRuleNeuronsCreated = neuralSets.weightedRuleNeurons.size();
            factNeuronsCreated = neuralSets.factNeurons.size();
        }

    }
}
