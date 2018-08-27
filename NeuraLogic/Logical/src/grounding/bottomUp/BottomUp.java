package grounding.bottomUp;

import constructs.example.LiftedExample;
import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.BodyAtom;
import constructs.template.Template;
import constructs.template.WeightedRule;
import grounding.Grounder;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import ida.utils.tuples.Pair;
import networks.evaluation.values.ScalarValue;
import networks.structure.NeuralNetwork;
import networks.structure.Weight;
import networks.structure.lrnnTypes.*;
import settings.Settings;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Gusta on 06.10.2016.
 */
public class BottomUp extends Grounder {
    private static final Logger LOG = Logger.getLogger(BottomUp.class.getName());

    HerbrandModel herbrandModel;

    /**
     * Temp structure (head -> rules -> ground bodies) for traversing the graph of groundings
     */
    LinkedHashMap<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> groundRules;

    public BottomUp(Settings settings) {
        super(settings);
        herbrandModel = new HerbrandModel();
        groundRules = new LinkedHashMap<>();
    }

    @Override
    public QueryNeuron ground(QueryAtom queryAtom, Template template) {
        return null;
    }

    /**
     * TODO create a streaming version for single examples?
     *
     * @param example
     * @param template
     * @return
     */
    @Override
    public NeuralNetwork ground(LiftedExample example, Template template) {

        Pair<Map<HornClause, WeightedRule>, Map<Literal, ValuedFact>> rulesAndFacts = mapToLogic(rulesAndFacts(example, template));
        Map<HornClause, WeightedRule> ruleMap = rulesAndFacts.r;
        Map<Literal, ValuedFact> factMap = rulesAndFacts.s;

        herbrandModel.loadModel(ruleMap.keySet(), factMap.keySet());

        for (Map.Entry<HornClause, WeightedRule> ruleEntry : ruleMap.entrySet()) {
            List<WeightedRule> groundings = herbrandModel.groundRules(ruleEntry.getValue(), ruleEntry.getKey());
            for (WeightedRule grounding : groundings) {
                Map<WeightedRule, List<WeightedRule>> rules2groundings =
                        groundRules.computeIfAbsent(grounding.head.getLiteral(), k -> new LinkedHashMap<>());
                List<WeightedRule> ruleGroundings =
                        rules2groundings.computeIfAbsent(ruleEntry.getValue(), k -> new LinkedList<>());
                ruleGroundings.add(grounding);
            }
        }

        NeuralNetwork neuralNetwork = networkFromGroundRules(groundRules, factMap);
        neuralNetwork.setId(example.getId());
        if (!sharedGroundings) {
            groundRules.clear();
            herbrandModel = new HerbrandModel();
        }
        return neuralNetwork;
    }

    private NeuralNetwork networkFromGroundRules(LinkedHashMap<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> groundRules, Map<Literal, ValuedFact> factMap) {
        Map<Literal, AtomNeuron> atomNeurons = new HashMap<>();
        Map<WeightedRule, AggregationNeuron> aggNeurons = new HashMap<>();
        Map<WeightedRule, RuleNeuron> ruleNeurons = new LinkedHashMap<>();


        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> headLiteral2rules : groundRules.entrySet()) {
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

        // once all neurons are created, connect rule neurons' inputs to atom neurons

        Set<NegationNeuron> negationNeurons = new HashSet<>();
        Set<FactNeuron> factNeurons = new HashSet<>();

        for (Map.Entry<WeightedRule, RuleNeuron> entry : ruleNeurons.entrySet()) {
            for (BodyAtom bodyAtom : entry.getKey().body) {
                Weight weight = bodyAtom.getConjunctWeight();

                AtomFact input = atomNeurons.get(bodyAtom.getLiteral()); //input is an atom neuron?
                if (input == null) { //input is a fact neuron!
                    ValuedFact valuedFact = factMap.get(bodyAtom.getLiteral());
                    if (valuedFact == null) {
                        LOG.severe("Error: no input found for this neuron!!: " + bodyAtom);
                    }
                    FactNeuron factNeuron = new FactNeuron(valuedFact);
                    factNeurons.add(factNeuron);
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
        NeuralNetwork network = new NeuralNetwork(atomNeurons.values(), aggNeurons.values(), ruleNeurons.values(), factNeurons, negationNeurons);
        return network;
    }
}