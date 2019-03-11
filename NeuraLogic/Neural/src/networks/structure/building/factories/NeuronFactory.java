package networks.structure.building.factories;

import constructs.example.ValuedFact;
import constructs.template.components.HeadAtom;
import constructs.template.components.WeightedRule;
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

    private int counter;

    /**
     * A mapping from ground literals and ground rules to respective neurons, which will be incrementally modified during the building process
     */
    public NeuronMaps neuronMaps;

    public NeuronFactory(Settings settings) {
        this.settings = settings;
    }

    public AtomNeuron createAtomNeuron(HeadAtom head) {
        Activation activation = head.activation != null ? head.activation : Activation.getActivationFunction(settings.atomNeuronActivation);
        State.Neural.Computation state = State.createBaseState(settings, activation);
        AtomNeuron<State.Neural.Computation> atomNeuron = new AtomNeuron<>(head, counter++, state);
        neuronMaps.atomNeurons.put(head.literal, atomNeuron);
        return atomNeuron;
    }

    public AggregationNeuron createAggNeuron(WeightedRule weightedRule) {
        Aggregation aggregation = weightedRule.aggregationFcn != null ? weightedRule.activationFcn : Aggregation.getAggregation(settings.aggNeuronActivation);
        State.Neural.Computation state = State.createBaseState(settings, aggregation);
        AggregationNeuron<State.Neural.Computation> aggregationNeuron = new AggregationNeuron<>(weightedRule, counter++, state);
        neuronMaps.aggNeurons.put(weightedRule, aggregationNeuron);
        return aggregationNeuron;
    }

    public RuleNeuron createRuleNeuron(WeightedRule groundRule) {
        Activation activation = groundRule.activationFcn != null ? groundRule.activationFcn : Activation.getActivationFunction(settings.ruleNeuronActivation);
        if (groundRule.crossProduct) {
            activation = new CrossProduct(activation);
        }
        State.Neural.Computation state = State.createBaseState(settings, activation);
        RuleNeuron<State.Neural.Computation> ruleNeuron = new RuleNeuron<>(groundRule, counter++, state);
        neuronMaps.ruleNeurons.put(groundRule, ruleNeuron);
        return ruleNeuron;
    }

    public FactNeuron createFactNeuron(ValuedFact fact) {
        FactNeuron result = neuronMaps.factNeurons.get(fact.literal);
        if (result == null) {    //fact neuron might have been created already and for them it is ok
            States.SimpleValue simpleValue = new States.SimpleValue(fact.getValue());
            FactNeuron factNeuron = new FactNeuron(fact, counter++, simpleValue);
            neuronMaps.factNeurons.put(fact.literal, factNeuron);
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
        return negationNeuron;
    }

    public WeightedRuleNeuron createWeightedRuleNeuron(WeightedRule groundRule) {
        Activation activation = groundRule.activationFcn != null ? groundRule.activationFcn : Activation.getActivationFunction(settings.ruleNeuronActivation);
        if (groundRule.crossProduct) {
            activation = new CrossProduct(activation);
        }
        State.Neural.Computation state = State.createBaseState(settings, activation);
        WeightedRuleNeuron<State.Neural.Computation> weightedRuleNeuron = new WeightedRuleNeuron<>(groundRule, counter++, state);
        neuronMaps.ruleNeurons.put(groundRule, weightedRuleNeuron);
        return weightedRuleNeuron;
    }
}