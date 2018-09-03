package grounding.bottomUp;

import constructs.example.LiftedExample;
import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.BodyAtom;
import constructs.template.Template;
import constructs.template.WeightedRule;
import constructs.template.templates.GraphTemplate;
import grounding.Grounder;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import ida.utils.tuples.Pair;
import networks.evaluation.values.ScalarValue;
import networks.structure.NeuralNetwork;
import networks.structure.Weight;
import networks.structure.lrnnTypes.*;
import org.jetbrains.annotations.NotNull;
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
     * Temp (for current pair of Template+Example) structure (head -> rules -> ground bodies) for traversing the graph of groundings
     */
    LinkedHashMap<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> groundRules;

    /**
     * Temp (for current pair of Template+Example) set of true ground facts
     */
    Map<Literal, ValuedFact> groundFacts;

    public BottomUp(Settings settings) {
        super(settings);
        herbrandModel = new HerbrandModel();
        groundRules = new LinkedHashMap<>();
    }

    @Override
    public QueryNeuron ground(QueryAtom queryAtom, Template template) {
        LOG.warning("Supervised bottom-up grounding with a normal flat Template instead of optimized GraphTemplate");
        Pair<Map<HornClause, WeightedRule>, Map<Literal, ValuedFact>> rulesAndFacts = mapToLogic(rulesAndFacts(queryAtom.evidence, template));
        Map<HornClause, WeightedRule> ruleMap = rulesAndFacts.r;
        Map<Literal, ValuedFact> factMap = rulesAndFacts.s;

        return null;
    }

    public QueryNeuron ground(QueryAtom queryAtom, GraphTemplate template) {
        if (settings.supervisedTemplateGraphPruning)
            template = template.prune(queryAtom);

        groundRulesAndFacts(queryAtom.evidence, template);

        //TODO next prune groundRules here
        NeuralNetwork neuralNetwork = networkFromGroundRules(groundRules, groundFacts);
        neuralNetwork.setId(queryAtom.evidence.getId());

        QueryNeuron qn = new QueryNeuron(queryAtom, neuralNetwork);

        cleanUp();
        return qn;
    }

    /**
     * TODO create a streaming version for single examples?
     * todo add version with continual rule creation instead of final substitutions
     *
     * @param example
     * @param template
     * @return
     */
    @Override
    public NeuralNetwork ground(LiftedExample example, Template template) {

        groundRulesAndFacts(example, template);

        NeuralNetwork neuralNetwork = networkFromGroundRules(groundRules, groundFacts);
        neuralNetwork.setId(example.getId());
        cleanUp();
        return neuralNetwork;
    }

    protected void cleanUp() {
        if (!sharedGroundings) {
            groundRules.clear();
            groundFacts.clear();
            herbrandModel = new HerbrandModel();
        }
    }

    @NotNull
    protected void groundRulesAndFacts(LiftedExample example, Template template) {
        Pair<Map<HornClause, WeightedRule>, Map<Literal, ValuedFact>> rulesAndFacts = mapToLogic(rulesAndFacts(example, template));
        Map<HornClause, WeightedRule> ruleMap = rulesAndFacts.r;
        groundFacts = rulesAndFacts.s;

        Set<Literal> facts = groundFacts.keySet();
        // add already inferred facts as a hack to speedup the Herbrand model calculation
        if (settings.inferTemplateFacts)
            facts = template.getAllFacts();

        herbrandModel.inferModel(ruleMap.keySet(), facts);

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

        return networkFromNeurons(factMap, atomNeurons, aggNeurons, ruleNeurons);
    }

    /**
     * Once all neurons are created, connect rule neurons' inputs to atom neurons/fact neurons
     *
     * @param factMap
     * @param atomNeurons
     * @param aggNeurons
     * @param ruleNeurons
     * @return
     */
    @NotNull
    private NeuralNetwork networkFromNeurons(Map<Literal, ValuedFact> factMap, Map<Literal, AtomNeuron> atomNeurons, Map<WeightedRule, AggregationNeuron> aggNeurons, Map<WeightedRule, RuleNeuron> ruleNeurons) {
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
        return new NeuralNetwork(atomNeurons.values(), aggNeurons.values(), ruleNeurons.values(), factNeurons, negationNeurons);
    }
}