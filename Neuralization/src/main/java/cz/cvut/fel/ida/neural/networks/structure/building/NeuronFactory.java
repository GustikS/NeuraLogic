package cz.cvut.fel.ida.neural.networks.structure.building;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.AtIndex;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Predicate;
import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundHeadRule;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundRule;
import cz.cvut.fel.ida.logic.constructs.template.components.HeadAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.States;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.*;
import cz.cvut.fel.ida.setup.Settings;

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

    private Value defaultFactValue;

    /**
     * A mapping from ground literals and ground rules to respective neurons, which will be incrementally modified during the building process
     */
    public NeuronMaps neuronMaps;

    public NeuronFactory(WeightFactory weightFactory, Settings settings) {
        this.weightFactory = weightFactory;
        this.settings = settings;
        atomOffset = new Weight(-10, "fixedAtomOffset", new ScalarValue(settings.defaultAtomNeuronOffset), true, true);
        ruleOffset = new Weight(-9, "fixedRuleOffset", new ScalarValue(settings.defaultRuleNeuronOffset), true, true);
        defaultFactValue = new ScalarValue(settings.defaultFactValue);  // this should not be just Value.ONE as the fact values need to be valid values for subsequent modification (e.g. function application)
    }

    public WeightedAtomNeuron createWeightedAtomNeuron(HeadAtom head, Literal groundHead) {
        Combination combination = head.getCombination() != null ? head.getCombination() : Combination.getFunction(settings.atomNeuronCombination);
        Transformation transformation = head.getTransformation() != null ? head.getTransformation() : Transformation.getFunction(settings.atomNeuronTransformation);

        State.Neural.Computation state = State.createBaseState(settings, combination, transformation);
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
        WeightedAtomNeuron<State.Neural.Computation> atomNeuron = new WeightedAtomNeuron<>(groundHead.toString(), offset, counter++, state);
        neuronMaps.atomNeurons.put(groundHead, atomNeuron);
        LOG.finest(() -> "Created atom neuron: " + atomNeuron);
        return atomNeuron;
    }

    public AtomNeuron createUnweightedAtomNeuron(HeadAtom head, Literal groundHead) {
        Combination combination = head.getCombination() != null ? head.getCombination() : Combination.getFunction(settings.atomNeuronCombination);
        Transformation transformation = head.getTransformation() != null ? head.getTransformation() : Transformation.getFunction(settings.atomNeuronTransformation);

        State.Neural.Computation state = State.createBaseState(settings, combination, transformation);
        AtomNeuron<State.Neural.Computation> atomNeuron = new AtomNeuron<>(groundHead.toString(), counter++, state);
        neuronMaps.atomNeurons.put(groundHead, atomNeuron);
        LOG.finest(() -> "Created atom neuron: " + atomNeuron);
        return atomNeuron;
    }

    public AggregationNeuron createAggNeuron(GroundHeadRule groundHeadRule) {
        WeightedRule weightedRule = groundHeadRule.weightedRule;
        Aggregation aggregation = weightedRule.getAggregationFcn() != null ? weightedRule.getAggregationFcn() : Aggregation.getFunction(settings.aggNeuronAggregation);

        State.Neural.Computation state = State.createBaseState(settings, aggregation, null);
        AggregationNeuron<State.Neural.Computation> aggregationNeuron = new AggregationNeuron<>(settings.fullAggNeuronStrings ? groundHeadRule.toFullString() : weightedRule.getOriginalString(), counter++, state);
        neuronMaps.aggNeurons.put(groundHeadRule, aggregationNeuron);
        LOG.finest(() -> "Created aggregation neuron: " + aggregationNeuron);
        return aggregationNeuron;
    }

    public SplittableAggregationNeuron createSplittableAggNeuron(GroundHeadRule groundHeadRule) {
        WeightedRule weightedRule = groundHeadRule.weightedRule;
        Aggregation aggregation = weightedRule.getAggregationFcn() != null ? weightedRule.getAggregationFcn() : Aggregation.getFunction(settings.aggNeuronAggregation);

        State.Neural.Computation state = State.createBaseState(settings, aggregation, null);
        SplittableAggregationNeuron<State.Neural.Computation> aggregationNeuron = new SplittableAggregationNeuron<>(settings.fullAggNeuronStrings ? groundHeadRule.toFullString() : weightedRule.getOriginalString(), counter++, state);
        neuronMaps.aggNeurons.put(groundHeadRule, aggregationNeuron);
        LOG.finest(() -> "Created splittable aggregation neuron: " + aggregationNeuron);
        return aggregationNeuron;
    }

    public AtomNeuron createSplittableAtomNeuron(Literal groundHead, SplittableAggregationNeuron splittableAggregationNeuron) {
        Combination combination = Combination.getFunction(settings.atomNeuronCombination);
        Transformation transformation = new AtIndex();

        State.Neural.Computation state = State.createBaseState(settings, combination, transformation);
        Literal head = new Literal(new Predicate("_" + groundHead.predicate().name, groundHead.predicate().arity), groundHead.isNegated(), groundHead.termList());

        AtomNeuron<State.Neural.Computation> atomNeuron = new AtomNeuron<>(head.toString(), counter++, state);
        neuronMaps.atomNeurons.put(head, atomNeuron);
        atomNeuron.addInput(splittableAggregationNeuron);

        LOG.finest(() -> "Created splittable atom neuron: " + atomNeuron);
        return atomNeuron;
    }


    public RuleNeuron createRuleNeuron(GroundRule groundRule) {
        WeightedRule weightedRule = groundRule.weightedRule;
        Combination combination = weightedRule.getCombination() != null ? weightedRule.getCombination() : Combination.getFunction(settings.ruleNeuronCombination);
        Transformation transformation = weightedRule.getTransformation() != null ? weightedRule.getTransformation() : Transformation.getFunction(settings.ruleNeuronTransformation);

        State.Neural.Computation state = State.createBaseState(settings, combination, transformation);
        RuleNeuron<State.Neural.Computation> ruleNeuron = new RuleNeuron<>(settings.fullRuleNeuronStrings ? groundRule.toFullString() : weightedRule.getOriginalString(), counter++, state);
        neuronMaps.ruleNeurons.put(groundRule, ruleNeuron);
        LOG.finest(() -> "Created rule neuron: " + ruleNeuron);
        return ruleNeuron;
    }

    public WeightedRuleNeuron createWeightedRuleNeuron(GroundRule groundRule) {
        WeightedRule weightedRule = groundRule.weightedRule;
        Combination combination = weightedRule.getCombination() != null ? weightedRule.getCombination() : Combination.getFunction(settings.ruleNeuronCombination);
        Transformation transformation = weightedRule.getTransformation() != null ? weightedRule.getTransformation() : Transformation.getFunction(settings.ruleNeuronTransformation);

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
        State.Neural.Computation state = State.createBaseState(settings, combination, transformation);
        WeightedRuleNeuron<State.Neural.Computation> weightedRuleNeuron = new WeightedRuleNeuron<>(settings.fullRuleNeuronStrings ? groundRule.toFullString() : weightedRule.getOriginalString(), offset, counter++, state);
        neuronMaps.ruleNeurons.put(groundRule, weightedRuleNeuron);
        LOG.finest(() -> "Created weightedRule neuron: " + weightedRuleNeuron);
        return weightedRuleNeuron;
    }

    public FactNeuron createFactNeuron(ValuedFact fact) {
        FactNeuron result = neuronMaps.factNeurons.get(fact.literal);
        if (result == null) {    //fact neuron might have been created already and for them it is ok
            States.SimpleValue simpleValue = new States.SimpleValue(fact.getValue() == null ? this.defaultFactValue : fact.getValue());     //todo this is incompatible with ParentCounter state for Fact neurons...
            FactNeuron factNeuron = new FactNeuron(fact.originalString, fact.weight, counter++, simpleValue);
            if (fact.weight != null && fact.weight.isLearnable()) {
                factNeuron.hasLearnableValue = true;
                simpleValue.isLearnable = true;
            }
            neuronMaps.factNeurons.put(fact.literal, factNeuron);
            LOG.finest(() -> "Created fact neuron: " + factNeuron);
            return factNeuron;
        } else {
            return result;
        }
    }

    public NegationNeuron createNegationNeuron(AtomFact atomFact, Transformation negation) {
        Transformation transformation = negation != null ? negation : Transformation.getFunction(settings.softNegation);
        State.Neural.Computation state = State.createBaseState(settings, null, transformation);
        NegationNeuron<State.Neural.Computation> negationNeuron = new NegationNeuron<>(atomFact, counter++, state);
        neuronMaps.negationNeurons.add(negationNeuron);
        LOG.finest(() -> "Created negation neuron: " + negationNeuron);
        return negationNeuron;
    }

}