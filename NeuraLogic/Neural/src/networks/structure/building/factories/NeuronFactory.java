package networks.structure.building.factories;

import constructs.example.ValuedFact;
import constructs.template.HeadAtom;
import constructs.template.WeightedRule;
import networks.evaluation.functions.Activation;
import networks.structure.metadata.states.State;
import networks.structure.metadata.states.States;
import networks.structure.neurons.types.*;
import settings.Settings;

import java.util.logging.Logger;

/**
 * The State type information of neurons is lost here, but can later be retrieved via double-dispatch.
 */
public class NeuronFactory {
    private static final Logger LOG = Logger.getLogger(NeuronFactory.class.getName());

    Settings settings;

    private int counter;

    public NeuronFactory(Settings settings) {
        this.settings = settings;
    }


    State.Computation getComputationState() {
        //todo next consider all possibilities
        if (settings.minibatch) {
            return new States.ComputationArray<>(new State.Computation.ValuePair[settings.minibatchSize]);
        } else {
            return new States.ComputationPair();
        }
    }

    /**
     *
     * @param head
     * @return
     */
    public AtomNeuron createAtomNeuron(HeadAtom head) {
        State.Computation state = getComputationState();
        AtomNeuron<State.Computation> atomNeuron = new AtomNeuron<>(head, counter++, state);
        atomNeuron.id = head.toString();
        return atomNeuron;
    }

    public AggregationNeuron createAggNeuron(WeightedRule weightedRule) {
        State.Computation state = getComputationState();
        AggregationNeuron<State.Computation> aggregationNeuron = new AggregationNeuron<>(weightedRule, counter++, state);
        aggregationNeuron.id = weightedRule.toString();
        return aggregationNeuron;
    }

    public RuleNeuron createRuleNeuron(WeightedRule groundRule) {
        State.Computation state = getComputationState();
        RuleNeuron<State.Computation> ruleNeuron = new RuleNeuron<>(groundRule, counter++, state);
        ruleNeuron.id = groundRule.toString();
        return ruleNeuron;
    }

    public FactNeuron createFactNeuron(ValuedFact fact) {
        FactNeuron factNeuron = new FactNeuron(fact, counter++);
        factNeuron.id = fact.toString();
        return factNeuron;
    }

    public NegationNeuron createNegationNeuron(AtomFact atomFact, Activation negation) {
        State.Computation state = getComputationState();
        NegationNeuron<State.Computation> negationNeuron = new NegationNeuron<>(atomFact, counter++, state, negation);
        negationNeuron.id = atomFact.toString();
        return negationNeuron;
    }

    public WeightedRuleNeuron createWeightedRuleNeuron(WeightedRule groundRule) {
        State.Computation state = getComputationState();
        WeightedRuleNeuron<State.Computation> weightedRuleNeuron = new WeightedRuleNeuron<>(groundRule, counter++, state);
        weightedRuleNeuron.id = groundRule.toString();
        return weightedRuleNeuron;
    }
}