package networks.structure.building.factories;

import constructs.example.ValuedFact;
import constructs.template.components.HeadAtom;
import constructs.template.components.WeightedRule;
import networks.computation.evaluation.functions.Activation;
import networks.structure.building.NeuronMaps;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.types.*;
import networks.structure.metadata.states.State;
import networks.structure.metadata.states.States;
import settings.Settings;

import java.util.logging.Logger;

/**
 * The State type information of neurons is lost here, but can later be retrieved via double-dispatch.
 */
public class NeuronFactory {
    private static final Logger LOG = Logger.getLogger(NeuronFactory.class.getName());

    Settings settings;

    private int counter;


    /**
     * A mapping from ground literals and ground rules to respective neurons, which will be modified during the building process
     */
    public NeuronMaps neuronMaps;

    public NeuronFactory(Settings settings) {
        this.settings = settings;
    }

    State.Neural getComputationState(BaseNeuron neuron) {
        if (settings.minibatchSize > 1 && neuron.isShared) {   //if there is minibatch, multiple threads will possibly be accessing the same neuron, i.e. we need array of states, one for each thread
            States.ComputationStateComposite<State.Neural.Computation> stateComposite = new States.ComputationStateComposite<>(new State.Neural.Computation[settings.minibatchSize]);
            for (int i = 0; i < stateComposite.states.length; i++) {
                stateComposite.states[i] = neuron.getRawState().clone();
            }
            return stateComposite;
        } else {    //otherwise just initialize the one state for this neuron
            return getBaseState(neuron.getClass());
        }
    }

    private State.Neural.Computation getBaseState(Class<? extends BaseNeuron> aClass) {
        if (aClass.)
    }


    /**
     * @param head
     * @return
     */
    public AtomNeuron createAtomNeuron(HeadAtom head) {
        State.Neural.Computation state = getComputationState();
        AtomNeuron<State.Neural.Computation> atomNeuron = new AtomNeuron<>(head, counter++, state);
        atomNeuron.id = head.toString();
        neuronMaps.atomNeurons.put(head.literal, atomNeuron);
        return atomNeuron;
    }

    public AggregationNeuron createAggNeuron(WeightedRule weightedRule) {
        State.Neural.Computation state = getComputationState();
        AggregationNeuron<State.Neural.Computation> aggregationNeuron = new AggregationNeuron<>(weightedRule, counter++, state);
        aggregationNeuron.id = weightedRule.toString();
        neuronMaps.aggNeurons.put(weightedRule, aggregationNeuron);
        return aggregationNeuron;
    }

    public RuleNeuron createRuleNeuron(WeightedRule groundRule) {
        State.Neural.Computation state = getComputationState();
        RuleNeuron<State.Neural.Computation> ruleNeuron = new RuleNeuron<>(groundRule, counter++, state);
        ruleNeuron.id = groundRule.toString();
        neuronMaps.ruleNeurons.put(groundRule, ruleNeuron);
        return ruleNeuron;
    }

    public FactNeuron createFactNeuron(ValuedFact fact) {
        FactNeuron factNeuron = new FactNeuron(fact, counter++);
        factNeuron.id = fact.toString();
        return factNeuron;
    }

    public NegationNeuron createNegationNeuron(AtomFact atomFact, Activation negation) {
        State.Neural.Computation state = getComputationState();
        NegationNeuron<State.Neural.Computation> negationNeuron = new NegationNeuron<>(atomFact, counter++, state, negation);
        negationNeuron.id = atomFact.toString();
        return negationNeuron;
    }

    public WeightedRuleNeuron createWeightedRuleNeuron(WeightedRule groundRule) {
        State.Neural.Computation state = getComputationState();
        WeightedRuleNeuron<State.Neural.Computation> weightedRuleNeuron = new WeightedRuleNeuron<>(groundRule, counter++, state);
        weightedRuleNeuron.id = groundRule.toString();
        return weightedRuleNeuron;
    }
}