package networks.structure.building.factories;

import constructs.example.ValuedFact;
import constructs.template.components.HeadAtom;
import constructs.template.components.WeightedRule;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.types.*;
import networks.structure.metadata.states.State;
import networks.structure.metadata.states.States;
import settings.Settings;
import sun.rmi.server.Activation;

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

    State.Computation getComputationState(Neuron neuron) {
        if (settings.minibatchSize > 1) {   //if there is minibatch, multiple threads will possibly be accessing the same neuron, i.e. we need array of states, one for each thread
            States.ComputationStateComposite<State.Computation> stateComposite = new States.ComputationStateComposite<>(new State.Computation[settings.minibatchSize]);
            for (int i = 0; i < stateComposite.states.length; i++) {
                stateComposite.states[i] = getBaseState(neuron);
            }
            return stateComposite;
        } else {    //otherwise just initialize the one state for this neuron
            return getBaseState(neuron);
        }
    }

    State.Computation getBaseState(Neuron neuron) {
        if (neuron.isShared) {

        } else {

        }
    }

    /**
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