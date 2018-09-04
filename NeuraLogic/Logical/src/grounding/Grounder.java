package grounding;

import constructs.example.LiftedExample;
import constructs.example.LogicSample;
import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.BodyAtom;
import constructs.template.Template;
import constructs.template.WeightedRule;
import grounding.bottomUp.BottomUp;
import grounding.topDown.TopDown;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import ida.utils.tuples.Pair;
import networks.evaluation.values.ScalarValue;
import networks.structure.NeuralNetwork;
import networks.structure.Weight;
import networks.structure.lrnnTypes.*;
import org.jetbrains.annotations.NotNull;
import settings.Settings;
import training.NeuralSample;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Gusta on 06.10.2016.
 *
 */
public abstract class Grounder {
    private static final Logger LOG = Logger.getLogger(Grounder.class.getName());
    protected Settings settings;

    protected boolean storeGroundings;

    public Grounder(Settings settings) {
        this.settings = settings;
        storeGroundings = settings.sharedGroundings;
    }

    public NeuralSample ground(LogicSample logicSample, GroundTemplate groundTemplate) {
        return new NeuralSample(logicSample.target, ground(logicSample.query, groundTemplate));
    }

    /**
     * Supervised grounding
     *
     * @param queryAtom
     * @return
     */
    public QueryNeuron ground(QueryAtom queryAtom, GroundTemplate groundTemplate) {
        NeuralNetwork neuralNetwork = networkFromGroundTemplate(queryAtom, groundTemplate);
        neuralNetwork.setId(queryAtom.evidence.getId());
        return new QueryNeuron(queryAtom, neuralNetwork);
    }

    /**
     * Unsupervised grounding
     *
     * @param groundTemplate
     * @return
     */
    public NeuralNetwork ground(GroundTemplate groundTemplate) {
        NeuralNetwork neuralNetwork = networkFromGroundTemplate(groundTemplate);
        neuralNetwork.setId(groundTemplate.getId());
        return neuralNetwork;
    }

    /**
     * @param example
     * @param template
     * @return
     */
    public abstract GroundTemplate groundRulesAndFacts(LiftedExample example, Template template);

    public Pair<Set<WeightedRule>, Set<ValuedFact>> rulesAndFacts(LiftedExample example, Template template) {
        LinkedHashSet<ValuedFact> flatFacts = new LinkedHashSet<>(example.flatFacts);
        flatFacts.addAll(example.conjunctions.stream().flatMap(conj -> conj.facts.stream()).collect(Collectors.toList()));
        flatFacts.addAll(template.facts);

        LinkedHashSet<WeightedRule> rules = new LinkedHashSet<>(template.rules);
        //rules.addAll(template.constraints) todo what to do with constraints?
        rules.addAll(example.rules);
        return new Pair<>(rules, flatFacts);
    }

    /**
     * Todo optimize this for the case when the template doesnt change with every example (remember the template rules)
     *
     * @param raf
     * @return
     */
    public Pair<Map<HornClause, WeightedRule>, Map<Literal, ValuedFact>> mapToLogic(Pair<Set<WeightedRule>, Set<ValuedFact>> raf) {
        Map<HornClause, WeightedRule> ruleMap = raf.r.stream().collect(Collectors.toMap(WeightedRule::toHornClause, wr -> wr, this::merge2rules));
        Map<Literal, ValuedFact> factMap = raf.s.stream().collect(Collectors.toMap(ValuedFact::getLiteral, vf -> vf, this::merge2facts));
        return new Pair<>(ruleMap, factMap);
    }

    /**
     * On a clash of two WeightedRules having the same underlying HornClause logic, add their weights (linearity of differentiation) //todo check
     *
     * @param a
     * @param b
     * @return
     */
    private WeightedRule merge2rules(WeightedRule a, WeightedRule b) {
        WeightedRule weightedRule = new WeightedRule(a);
        weightedRule.weight = new Weight(a.weight.name, a.weight.value.add(b.weight.value), a.weight.isFixed);
        return weightedRule;
    }

    /**
     * On a clash of two ValuedFacts having the same underlying Literal logic, add their weights (linearity of differentiation) //todo check
     *
     * @param a
     * @param b
     * @return
     */
    private ValuedFact merge2facts(ValuedFact a, ValuedFact b) {
        return new ValuedFact(a.getOffsettedPredicate(), a.getLiteral().termList(), a.getLiteral().isNegated(), a.getFactValue().add(b.getFactValue()));
    }

    public static Grounder getGrounder(Settings settings) {
        switch (settings.grounding) {
            case BUP:
                return new BottomUp(settings);
            case TDOWN:
                return new TopDown(settings);
            default:
                return new BottomUp(settings);
        }
    }


    /**
     * Todo next -
     * @param queryAtom
     * @param groundTemplate
     * @return
     */
    private List<QueryNeuron> networkFromGroundTemplate(QueryAtom queryAtom, GroundTemplate groundTemplate) {

    }

    private NeuralNetwork networkFromGroundTemplate(GroundTemplate groundTemplate) {
        Map<Literal, AtomNeuron> atomNeurons = new HashMap<>();
        Map<WeightedRule, AggregationNeuron> aggNeurons = new HashMap<>();
        Map<WeightedRule, RuleNeuron> ruleNeurons = new LinkedHashMap<>();
        Map<Literal, FactNeuron> factNeurons = new HashMap<>();

        //rules
        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> headLiteral2rules : groundTemplate.groundRules.entrySet()) {
            AtomNeuron headAtom;
            if ((headAtom = atomNeurons.get(headLiteral2rules.getKey())) == null) {
                headAtom = new AtomNeuron(headLiteral2rules.getValue().entrySet().iterator().next().getValue().get(0).head); //it doesnt matter which head
                atomNeurons.put(headLiteral2rules.getKey(), headAtom);
            }
            for (Map.Entry<WeightedRule, List<WeightedRule>> rules2groundings : headLiteral2rules.getValue().entrySet()) {
                AggregationNeuron aggNeuron;
                if ((aggNeuron = aggNeurons.get(rules2groundings.getKey())) == null) {
                    aggNeuron = new AggregationNeuron(rules2groundings.getKey());
                    aggNeurons.put(rules2groundings.getKey(), aggNeuron);
                }
                headAtom.addInput(aggNeuron, rules2groundings.getKey().weight);

                for (WeightedRule grounding : rules2groundings.getValue()) {
                    RuleNeuron ruleNeuron;
                    if ((ruleNeuron = ruleNeurons.get(grounding)) == null) {
                        ruleNeuron = new RuleNeuron(grounding);
                        ruleNeurons.put(grounding, ruleNeuron);
                    }
                    aggNeuron.addInput(ruleNeuron, new Weight(new ScalarValue(settings.aggNeuronInputWeight)));
                }
            }
        }

        //facts
        for (Map.Entry<Literal, ValuedFact> factEntry : groundTemplate.groundFacts.entrySet()) {
            FactNeuron factNeuron = new FactNeuron(factEntry.getValue());
            factNeurons.put(factEntry.getKey(), factNeuron);
        }

        return networkFromNeurons(factNeurons, atomNeurons, aggNeurons, ruleNeurons);
    }

    /**
     * Once all neurons are created, connect rule neurons' inputs to atom neurons/fact neurons
     *
     * @param factNeurons
     * @param atomNeurons
     * @param aggNeurons
     * @param ruleNeurons
     * @return
     */
    @NotNull
    private NeuralNetwork networkFromNeurons(Map<Literal, FactNeuron> factNeurons, Map<Literal, AtomNeuron> atomNeurons, Map<WeightedRule, AggregationNeuron> aggNeurons, Map<WeightedRule, RuleNeuron> ruleNeurons) {
        Set<NegationNeuron> negationNeurons = new HashSet<>();

        for (Map.Entry<WeightedRule, RuleNeuron> entry : ruleNeurons.entrySet()) {
            for (BodyAtom bodyAtom : entry.getKey().body) {
                Weight weight = bodyAtom.getConjunctWeight();

                AtomFact input = atomNeurons.get(bodyAtom.getLiteral()); //input is an atom neuron?
                if (input == null) { //input is a fact neuron!
                    FactNeuron factNeuron = factNeurons.get(bodyAtom.getLiteral());
                    if (factNeuron == null) {
                        LOG.severe("Error: no input found for this neuron!!: " + bodyAtom);
                    }
                    input = factNeuron;
                    weight = new Weight(new ScalarValue(settings.aggNeuronInputWeight));
                }
                if (bodyAtom.isNegated()) {
                    NegationNeuron negationNeuron = new NegationNeuron(input, bodyAtom.getNegationActivation());
                    negationNeurons.add(negationNeuron);
                    input = negationNeuron;
                }
                entry.getValue().addInput(input, weight);
            }
        }
        return new NeuralNetwork(atomNeurons.values(), aggNeurons.values(), ruleNeurons.values(), factNeurons.values(), negationNeurons);
    }
}