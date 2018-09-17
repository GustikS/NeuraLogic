package grounding;

import constructs.example.LiftedExample;
import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.BodyAtom;
import constructs.template.Template;
import constructs.template.WeightedRule;
import grounding.bottomUp.BottomUp;
import grounding.topDown.TopDown;
import ida.ilp.logic.Clause;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.subsumption.Matching;
import ida.utils.tuples.Pair;
import networks.evaluation.values.ScalarValue;
import networks.structure.NeuralNetwork;
import networks.structure.Neuron;
import networks.structure.Weight;
import networks.structure.lrnnTypes.*;
import org.jetbrains.annotations.NotNull;
import settings.Settings;
import training.NeuralSample;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Gusta on 06.10.2016.
 */
public abstract class Grounder {
    private static final Logger LOG = Logger.getLogger(Grounder.class.getName());
    protected Settings settings;

    public Grounder(Settings settings) {
        this.settings = settings;
    }

    public List<NeuralSample> neuralize(GroundingSample groundingSample) {
        List<QueryNeuron> queryNeurons = neuralize(groundingSample.query, groundingSample.grounding.getGrounding());
        List<NeuralSample> samples = new ArrayList<>();
        for (QueryNeuron queryNeuron : queryNeurons) {
            samples.add(new NeuralSample(groundingSample.target, queryNeuron));
        }
        return samples;
    }

    /**
     * Supervised grounding (recursive network construction top-down from grounded rules)
     *
     * @param queryAtom
     * @return
     */
    public List<QueryNeuron> neuralize(QueryAtom queryAtom, GroundTemplate groundTemplate) { // - todo test if all correct in sequential sharing mode!!!
        List<Literal> queryLiterals = new ArrayList<>();

        Matching matching = new Matching();
        Set<Literal> closedSet = new HashSet<>();

        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> entry : groundTemplate.groundRules.entrySet()) {
            if (queryAtom.headAtom.literal.predicate().equals(entry.getKey().predicate()) && matching.subsumption(new Clause(queryAtom.headAtom.literal), new Clause(entry.getKey()))) { //todo check this method
                queryLiterals.add(entry.getKey());

                if (!settings.forceFullNetworks) {
                    closedSet.add(entry.getKey());
                    recurseNeurons(entry.getKey(), groundTemplate, closedSet);
                }
            }
        }

        if (settings.forceFullNetworks) {
            groundTemplate.neuralNetwork = neuralize(groundTemplate);
        } else {
            groundTemplate.neuronMaps.factNeurons.putAll(neuronsFromFacts(groundTemplate.groundFacts));
            groundTemplate.neuralNetwork = connectRuleNeurons(groundTemplate.neuronMaps);
            groundTemplate.neuralNetwork.setId(groundTemplate.getId());
        }

        List<QueryNeuron> queryNeurons = new ArrayList<>();
        for (Literal queryLiteral : queryLiterals) {
            AtomNeuron atomNeuron = groundTemplate.neuronMaps.atomNeurons.get(queryLiteral);
            QueryNeuron queryNeuron = new QueryNeuron(queryAtom.ID, queryAtom.position, queryAtom.importance, atomNeuron, groundTemplate.neuralNetwork);
            queryNeurons.add(queryNeuron);
        }
        return queryNeurons;
    }

    /**
     * Unsupervised grounding (flat network construction from all grounded rules)
     *
     * @param groundTemplate
     * @return
     */
    public NeuralNetwork neuralize(GroundTemplate groundTemplate) {
        GroundTemplate.NeuronMaps neuronMaps = groundTemplate.neuronMaps;
        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> entry : groundTemplate.groundRules.entrySet()) {
            addNeuronsForHeadLiteral(new Pair<>(entry.getKey(), entry.getValue()), groundTemplate.neuronMaps);
        }

        neuronMaps.factNeurons.putAll(neuronsFromFacts(groundTemplate.groundFacts));
        NeuralNetwork neuralNetwork = connectRuleNeurons(groundTemplate.neuronMaps);
        neuralNetwork.setId(groundTemplate.getId());
        return neuralNetwork;
    }

    /**
     * The theorem proving part - returns ground rules and facts wrapped in a GroundTemplate - to be implemented by subclasses
     *
     * @param example
     * @param template
     * @return
     */
    public abstract GroundTemplate groundRulesAndFacts(LiftedExample example, Template template);

    /**
     * The theorem proving part - with reuse of some previous grounding
     *
     * @param example
     * @param template
     * @param reuse
     * @return
     */
    public abstract GroundTemplate groundRulesAndFacts(LiftedExample example, Template template, GroundTemplate reuse);


    /**
     * Consume all samples, share all facts and rules between them, then ground as a single big sample.
     * Stream TERMINATING operation!
     *
     * @param samples
     * @return
     */
    public Stream<GroundingSample> globalGroundingSample(Stream<GroundingSample> samples) {
        List<GroundingSample> sampleList = samples.collect(Collectors.toList());
        Template template = new Template();
        LiftedExample liftedExample = new LiftedExample();

        template.addAllFrom(sampleList.get(0).template);
        for (int i = 1; i < sampleList.size(); i++) {
            if (sampleList.get(i).template != sampleList.get(i - 1).template)
                template.addAllFrom(sampleList.get(i).template);
        }
        liftedExample.addAllFrom(sampleList.get(0).query.evidence);
        for (int i = 1; i < sampleList.size(); i++) {
            if (sampleList.get(i).query.evidence != sampleList.get(i - 1).query.evidence)
                liftedExample.addAllFrom(sampleList.get(i).query.evidence);
        }

        GroundTemplate groundTemplate = groundRulesAndFacts(liftedExample, template);

        sampleList.forEach(sample -> sample.grounding.setGrounding(groundTemplate));
        return sampleList.stream();
    }

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
     * On a clash of two WeightedRules having the same underlying HornClause logic, add their weights (linearity of differentiation) //todo next correct for explicit weight sharing - cannot just add shared weight
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
     * On a clash of two ValuedFacts having the same underlying Literal logic, add their Values (linearity of differentiation) //todo check
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
     * Recursively build the network top-down, taking only ground rules in support of the given literal into account
     *
     * @param literal
     * @param groundTemplate
     * @param closedSet
     */
    private void recurseNeurons(@NotNull Literal literal, GroundTemplate groundTemplate, Set<Literal> closedSet) {
        if (closedSet.contains(literal)) {
            return;
        }
        closedSet.add(literal);

        LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>> ruleMap = groundTemplate.groundRules.get(literal);
        addNeuronsForHeadLiteral(new Pair<>(literal, ruleMap), groundTemplate.neuronMaps);

        for (LinkedHashSet<WeightedRule> groundings : ruleMap.values()) {
            for (WeightedRule grounding : groundings) {
                for (BodyAtom bodyAtom : grounding.body) {
                    recurseNeurons(bodyAtom.literal, groundTemplate, closedSet);
                }
            }
        }
    }


    /**
     * TODO ERROR - in sequential sharing, we are modifying the previous neurons here (adding inputs to them) - the previous neurons should stay same, we should only add structures...
     * ->
     *
     * @param headLiteral2rules
     * @param neuronMaps
     * @return
     */
    private GroundTemplate.NeuronMaps addNeuronsForHeadLiteral(Pair<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> headLiteral2rules, GroundTemplate.NeuronMaps neuronMaps) {

        boolean newAtomNeuron = false;
        AtomNeuron headAtomNeuron;
        if ((headAtomNeuron = neuronMaps.atomNeurons.get(headLiteral2rules.r)) == null) {
            newAtomNeuron = true;
            headAtomNeuron = new AtomNeuron(headLiteral2rules.s.entrySet().iterator().next().getValue().iterator().next().head); //it doesn't matter which head
            neuronMaps.atomNeurons.put(headLiteral2rules.r, headAtomNeuron);
        }
        for (Map.Entry<WeightedRule, LinkedHashSet<WeightedRule>> rules2groundings : headLiteral2rules.s.entrySet()) {
            boolean newAggNeuron = false;
            AggregationNeuron aggNeuron;
            if ((aggNeuron = neuronMaps.aggNeurons.get(rules2groundings.getKey())) == null) {
                newAggNeuron = true;
                aggNeuron = new AggregationNeuron(rules2groundings.getKey());
                neuronMaps.aggNeurons.put(rules2groundings.getKey(), aggNeuron);
            }
            if (newAtomNeuron) {
                headAtomNeuron.addInput(aggNeuron, rules2groundings.getKey().weight);
            } else {
                LOG.info("Warning-  modifying previous state - Creating input overmapping for this Atom neuron: " + headAtomNeuron);
                ArrayList<Pair<Neuron, Weight>> inputs = neuronMaps.extraInputMapping.computeIfAbsent(headAtomNeuron, f -> new ArrayList<>());
                inputs.add(new Pair<>(aggNeuron, rules2groundings.getKey().weight));
            }
            for (WeightedRule grounding : rules2groundings.getValue()) {
                RuleNeuron ruleNeuron;
                if ((ruleNeuron = neuronMaps.ruleNeurons.get(grounding)) == null) {
                    ruleNeuron = new RuleNeuron(grounding);
                    neuronMaps.ruleNeurons.put(grounding, ruleNeuron);
                } else {
                    LOG.warning("Inconsistency - Rule neuron already contained in neuronmap!!");
                }
                if (newAggNeuron) {
                    aggNeuron.addInput(ruleNeuron, new Weight(new ScalarValue(settings.aggNeuronInputWeight)));
                } else {
                    LOG.info("Warning-  modifying previous state - Creating input overmapping for this Agg neuron: " + aggNeuron);
                    ArrayList<Pair<Neuron, Weight>> inputs = neuronMaps.extraInputMapping.computeIfAbsent(aggNeuron, f -> new ArrayList<>());
                    inputs.add(new Pair<>(ruleNeuron, new Weight(new ScalarValue(settings.aggNeuronInputWeight))));
                }
            }
        }

        return neuronMaps;
    }

    private Map<Literal, FactNeuron> neuronsFromFacts(Map<Literal, ValuedFact> groundFacts) {
        Map<Literal, FactNeuron> factNeurons = new HashMap<>();
        for (Map.Entry<Literal, ValuedFact> factEntry : groundFacts.entrySet()) {
            FactNeuron factNeuron = new FactNeuron(factEntry.getValue());
            factNeurons.put(factEntry.getKey(), factNeuron);
        }
        return factNeurons;
    }

    /**
     * Once all neurons are created, connect rule neurons' inputs to atom neurons/fact neurons.
     * Only newly created rule nerons are processed.
     *
     * @return
     */
    @NotNull
    private NeuralNetwork connectRuleNeurons(GroundTemplate.NeuronMaps neuronMaps) {   //todo next change back to neuronMaps input arguments
        Set<NegationNeuron> negationNeurons = new HashSet<>();

        for (Map.Entry<WeightedRule, RuleNeuron> entry : neuronMaps.ruleNeurons.entrySet()) {
            if (entry.getValue().inputCount() == entry.getKey().body.size()) {
                continue;   //this rule neuron is already connected (was created and taken from previous sample), connect only the newly created RuleNeurons
            }
            for (BodyAtom bodyAtom : entry.getKey().body) {
                Weight weight = bodyAtom.getConjunctWeight();

                AtomFact input = neuronMaps.atomNeurons.get(bodyAtom.getLiteral()); //input is an atom neuron?
                if (input == null) { //input is a fact neuron!
                    FactNeuron factNeuron = neuronMaps.factNeurons.get(bodyAtom.getLiteral());
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
        NeuralNetwork neuralNetwork = new NeuralNetwork(neuronMaps.atomNeurons.values(), neuronMaps.aggNeurons.values(), neuronMaps.ruleNeurons.values(), neuronMaps.factNeurons.values(), negationNeurons);
        neuralNetwork.extraInputMapping = neuronMaps.extraInputMapping;
        return neuralNetwork;
    }
}