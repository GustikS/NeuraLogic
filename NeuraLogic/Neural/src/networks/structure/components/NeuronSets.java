package networks.structure.components;

import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.types.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NeuronSets {

    public List<AtomNeuron> atomNeurons;
    public List<AggregationNeuron> aggNeurons;
    public List<RuleNeuron> ruleNeurons;
    public List<WeightedRuleNeuron> weightedRuleNeurons;
    public List<FactNeuron> factNeurons;
    public List<NegationNeuron> negationNeurons;

    List<BaseNeuron> roots;
    List<BaseNeuron> leaves;

    public NeuronSets(Collection<AtomNeuron> atomNeurons, Collection<AggregationNeuron> aggregationNeurons, Collection<RuleNeurons> ruleNeurons, Collection<FactNeuron> factNeurons, Collection<NegationNeuron> negationNeurons) {

        this.atomNeurons = new ArrayList<>(atomNeurons);
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
}
