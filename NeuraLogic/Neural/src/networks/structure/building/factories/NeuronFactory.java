package networks.structure.building.factories;

import constructs.building.factories.WeightFactory;
import constructs.example.ValuedFact;
import constructs.template.components.GroundHeadRule;
import constructs.template.components.GroundRule;
import constructs.template.components.HeadAtom;
import constructs.template.components.WeightedRule;
import evaluation.functions.Activation;
import evaluation.functions.Aggregation;
import evaluation.functions.CrossProduct;
import evaluation.values.ScalarValue;
import evaluation.values.Value;
import ida.ilp.logic.Literal;
import networks.structure.building.NeuronMaps;
import networks.structure.components.neurons.states.State;
import networks.structure.components.neurons.states.States;
import networks.structure.components.neurons.types.*;
import networks.structure.components.weights.Weight;
import settings.Settings;

import java.util.logging.Logger;

/**
 * The State type information of neurons is lost here, but can later be retrieved via double-dispatch.
 */
public class NeuronFactory {
    private static final Logger LOG = Logger.getLogger(NeuronFactory.class.getName());

    private WeightFactory weightFactory;
    Settings settings;

    private int counter = 0;

    private Weight atomOffset;
    private Weight ruleOffset;

    private Value defaultFactValue = Value.ONE;

    /**
     * A mapping from ground literals and ground rules to respective neurons, which will be incrementally modified during the building process
     */
    public NeuronMaps neuronMaps;

    public NeuronFactory(WeightFactory weightFactory, Settings settings) {
        this.weightFactory = weightFactory;
        this.settings = settings;
        atomOffset = weightFactory.construct("fixedAtomOffset", new ScalarValue(settings.defaultAtomNeuronOffset), true, true);
        ruleOffset = weightFactory.construct("fixedRuleOffset", new ScalarValue(settings.defaultRuleNeuronOffset), true, true);
        if (settings.defaultFactValue != 1){
            defaultFactValue = new ScalarValue(settings.defaultFactValue);
        }
    }

    public WeightedAtomNeuron createWeightedAtomNeuron(HeadAtom head, Literal groundHead) {
        Activation activation = head.getActivation() != null ? head.getActivation() : Activation.getActivationFunction(settings.atomNeuronActivation);
        State.Neural.Computation state = State.createBaseState(settings, activation);
        Weight offset = head.getOffset();
        if (offset == null) {
            if (settings.defaultAtomOffsetsLearnable) {
                if (settings.defaultAtomNeuronOffset != 0) {
                    offset = weightFactory.construct(new ScalarValue(settings.defaultAtomNeuronOffset), false, true);
                } else {
                    offset = weightFactory.construct(new ScalarValue(settings.defaultAtomNeuronOffset), false, false);
                }
            } else {
                if (settings.defaultAtomNeuronOffset != 0) {
                    offset = atomOffset;
                }
            }
        }
        WeightedAtomNeuron<State.Neural.Computation> atomNeuron = new WeightedAtomNeuron<>(groundHead, offset, counter++, state);
        neuronMaps.atomNeurons.put(groundHead, atomNeuron);
        LOG.finest("Created atom neuron: " + atomNeuron);
        return atomNeuron;
    }

    public AtomNeuron createUnweightedAtomNeuron(HeadAtom head, Literal groundHead) {
        Activation activation = head.getActivation() != null ? head.getActivation() : Activation.getActivationFunction(settings.atomNeuronActivation);
        State.Neural.Computation state = State.createBaseState(settings, activation);
        AtomNeuron<State.Neural.Computation> atomNeuron = new AtomNeuron<>(groundHead, counter++, state);
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
        Weight offset = weightedRule.getOffset();
        if (offset == null) {
            if (settings.defaultRuleOffsetsLearnable) {
                if (settings.defaultRuleNeuronOffset != 0) {
                    offset = weightFactory.construct(new ScalarValue(settings.defaultRuleNeuronOffset), false, true);
                } else {
                    offset = weightFactory.construct(new ScalarValue(settings.defaultRuleNeuronOffset), false, false);
                }
            } else {
                if (settings.defaultRuleNeuronOffset != 0) {
                    offset = atomOffset;
                }
            }
        }
        State.Neural.Computation state = State.createBaseState(settings, activation);
        WeightedRuleNeuron<State.Neural.Computation> weightedRuleNeuron = new WeightedRuleNeuron<>(settings.fullRuleNeuronStrings ? groundRule.toFullString() : weightedRule.getOriginalString(), offset, counter++, state);
        neuronMaps.ruleNeurons.put(groundRule, weightedRuleNeuron);
        LOG.finest("Created weightedRule neuron: " + weightedRuleNeuron);
        return weightedRuleNeuron;
    }

    public FactNeuron createFactNeuron(ValuedFact fact) {
        FactNeuron result = neuronMaps.factNeurons.get(fact.literal);
        if (result == null) {    //fact neuron might have been created already and for them it is ok
            States.SimpleValue simpleValue = new States.SimpleValue(fact.getValue() == null ? this.defaultFactValue : fact.getValue());
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