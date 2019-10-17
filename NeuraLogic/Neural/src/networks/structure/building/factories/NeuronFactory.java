package networks.structure.building.factories;

import constructs.example.ValuedFact;
import constructs.template.components.GroundHeadRule;
import constructs.template.components.GroundRule;
import constructs.template.components.HeadAtom;
import constructs.template.components.WeightedRule;
import ida.ilp.logic.Literal;
import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.functions.CrossProduct;
import networks.structure.building.NeuronMaps;
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

    private int counter = 0;

    /**
     * A mapping from ground literals and ground rules to respective neurons, which will be incrementally modified during the building process
     */
    public NeuronMaps neuronMaps;

    public NeuronFactory(Settings settings) {
        this.settings = settings;
    }

    public AtomNeuron createAtomNeuron(HeadAtom head, Literal groundHead) {
        Activation activation = head.activation != null ? head.activation : Activation.getActivationFunction(settings.atomNeuronActivation);
        State.Neural.Computation state = State.createBaseState(settings, activation);
        AtomNeuron<State.Neural.Computation> atomNeuron = new AtomNeuron<>(groundHead, head.getOffset(), counter++, state);
        neuronMaps.atomNeurons.put(groundHead, atomNeuron);
        LOG.finest("Created atom neuron: " + atomNeuron);
        return atomNeuron;
    }

    public AggregationNeuron createAggNeuron(GroundHeadRule groundHeadRule) {
        WeightedRule weightedRule = groundHeadRule.weightedRule;
        Aggregation aggregation = weightedRule.getAggregationFcn() != null ? weightedRule.getActivationFcn() : Aggregation.getAggregation(settings.aggNeuronActivation);
        State.Neural.Computation state = State.createBaseState(settings, aggregation);
        AggregationNeuron<State.Neural.Computation> aggregationNeuron = new AggregationNeuron<>(settings.fullAggNeuronStrings ? groundHeadRule.toFullString() : weightedRule.getOriginalString(), counter++, state);
        neuronMaps.aggNeurons.put(groundHeadRule, aggregationNeuron);
        LOG.finest("Created aggregation neuron: " + aggregationNeuron);
        return aggregationNeuron;
    }

    public RuleNeuron createRuleNeuron(GroundRule groundRule) {
        WeightedRule weightedRule = groundRule.weightedRule;
        Activation activation = weightedRule.getActivationFcn() != null ? weightedRule.getActivationFcn() : Activation.getActivationFunction(settings.ruleNeuronActivation);
        if (weightedRule.isCrossProduct()) {
            activation = new CrossProduct(activation);
        }
        State.Neural.Computation state = State.createBaseState(settings, activation);
        RuleNeuron<State.Neural.Computation> ruleNeuron = new RuleNeuron<>(settings.fullRuleNeuronStrings ? groundRule.toFullString() : weightedRule.getOriginalString(), counter++, state);
        neuronMaps.ruleNeurons.put(groundRule, ruleNeuron);
        LOG.finest("Created rule neuron: " + ruleNeuron);
        return ruleNeuron;
    }

    public WeightedRuleNeuron createWeightedRuleNeuron(GroundRule groundRule) {
        WeightedRule weightedRule = groundRule.weightedRule;
        Activation activation = weightedRule.getActivationFcn() != null ? weightedRule.getActivationFcn() : Activation.getActivationFunction(settings.ruleNeuronActivation);
        if (weightedRule.isCrossProduct()) {
            activation = new CrossProduct(activation);
        }
        State.Neural.Computation state = State.createBaseState(settings, activation);
        WeightedRuleNeuron<State.Neural.Computation> weightedRuleNeuron = new WeightedRuleNeuron<>(settings.fullRuleNeuronStrings ? groundRule.toFullString() : weightedRule.getOriginalString(), weightedRule.getOffset(), counter++, state);
        neuronMaps.ruleNeurons.put(groundRule, weightedRuleNeuron);
        LOG.finest("Created weightedRule neuron: " + weightedRuleNeuron);
        return weightedRuleNeuron;
    }

    public FactNeuron createFactNeuron(ValuedFact fact) {
        FactNeuron result = neuronMaps.factNeurons.get(fact.literal);
        if (result == null) {    //fact neuron might have been created already and for them it is ok
            States.SimpleValue simpleValue = new States.SimpleValue(fact.getValue());
            FactNeuron factNeuron = new FactNeuron(fact, counter++, simpleValue);
            neuronMaps.factNeurons.put(fact.literal, factNeuron);
            LOG.finest("Created fact neuron: " + factNeuron);
            return factNeuron;
        } else {
            return result;
        }
    }

    public NegationNeuron createNegationNeuron(AtomFact atomFact, Activation negation) {
        Activation activation = negation != null ? negation : Activation.getActivationFunction(settings.negation);
        State.Neural.Computation state = State.createBaseState(settings, activation);
        NegationNeuron<State.Neural.Computation> negationNeuron = new NegationNeuron<>(atomFact, counter++, state);
        neuronMaps.negationNeurons.add(negationNeuron);
        LOG.finest("Created negation neuron: " + negationNeuron);
        return negationNeuron;
    }

}