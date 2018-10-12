package networks.structure.building;

import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.BodyAtom;
import constructs.template.WeightedRule;
import grounding.GroundTemplate;
import grounding.Grounder;
import grounding.GroundingSample;
import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.subsumption.Matching;
import ida.utils.tuples.Pair;
import networks.structure.NeuralProcessingSample;
import networks.structure.building.factories.NeuralBuilder;
import networks.structure.metadata.inputMappings.LinkedNeuronMapping;
import networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import networks.structure.networks.types.DetailedNetwork;
import networks.structure.neurons.QueryNeuron;
import networks.structure.neurons.WeightedNeuron;
import networks.structure.neurons.types.*;
import networks.structure.weights.Weight;
import org.jetbrains.annotations.NotNull;
import settings.Settings;

import java.util.*;
import java.util.logging.Logger;

/**
 * Class responsible for building neural networks from GroundTemplates (ground rules and facts)
 */
public class Neuralizer {
    private static final Logger LOG = Logger.getLogger(Neuralizer.class.getName());
    private Settings settings;

    /**
     * The single point of reference for creating anything neural from logic parts found by this grounder
     */
    public NeuralBuilder neuralBuilder;

    public Neuralizer(Settings settings, NeuralBuilder neuralBuilder){
        this.settings = settings;
        this.neuralBuilder = neuralBuilder;
    }

    public Neuralizer(Grounder grounder) {
        this.settings = grounder.settings;
        this.neuralBuilder = new NeuralBuilder(settings);
        this.neuralBuilder.weightFactory = grounder.weightFactory;
    }

    /**
     * A single grounding sample may produce multiple neural samples because the query may be matched multiple times
     * @param groundingSample
     * @return
     */
    public List<NeuralProcessingSample> neuralize(GroundingSample groundingSample) {
        List<QueryNeuron> queryNeurons = neuralize(groundingSample.query, groundingSample.grounding.getGrounding());
        List<NeuralProcessingSample> samples = new ArrayList<>();
        for (QueryNeuron queryNeuron : queryNeurons) {
            samples.add(new NeuralProcessingSample(groundingSample.target, queryNeuron));
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
            groundTemplate.neuralNetwork = connectRuleNeurons2atoms(groundTemplate.neuronMaps);
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
    public DetailedNetwork neuralize(GroundTemplate groundTemplate) {
        GroundTemplate.NeuronMaps neuronMaps = groundTemplate.neuronMaps;
        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> entry : groundTemplate.groundRules.entrySet()) {
            addNeuronsForHeadLiteralRules(new Pair<>(entry.getKey(), entry.getValue()), groundTemplate.neuronMaps);
        }

        neuronMaps.factNeurons.putAll(neuronsFromFacts(groundTemplate.groundFacts));
        DetailedNetwork neuralNetwork = connectRuleNeurons2atoms(groundTemplate.neuronMaps);
        neuralNetwork.setId(groundTemplate.getId());
        return neuralNetwork;
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
        addNeuronsForHeadLiteralRules(new Pair<>(literal, ruleMap), groundTemplate.neuronMaps);

        for (LinkedHashSet<WeightedRule> groundings : ruleMap.values()) {
            for (WeightedRule grounding : groundings) {
                for (BodyAtom bodyAtom : grounding.body) {
                    recurseNeurons(bodyAtom.literal, groundTemplate, closedSet);
                }
            }
        }
    }


    /**
     * Given a literal together with all corresponding rules where it appears as a head, create AtomNeuron -> AggNeurons -> RuleNeurons neurons and connections.
     * Reuse existing neuronMaps, i.e., detect if a required neuron already exists in these,
     * - if so, then we need to add extra input mapping not to change the previously existing neurons, while still being able to reuse them.
     * <p>
     * todo next replace neuron creation with factory
     *
     * @param headLiteral2rules
     * @param neuronMaps
     * @return
     */
    private GroundTemplate.NeuronMaps addNeuronsForHeadLiteralRules(Pair<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> headLiteral2rules, GroundTemplate.NeuronMaps neuronMaps) {

        boolean newAtomNeuron = false;
        AtomNeuron headAtomNeuron;
        if ((headAtomNeuron = neuronMaps.atomNeurons.get(headLiteral2rules.r)) == null) {
            newAtomNeuron = true;
            headAtomNeuron = neuralBuilder.neuronFactory.createAtomNeuron(headLiteral2rules.s.entrySet().iterator().next().getValue().iterator().next().head); //it doesn't matter which head
            neuronMaps.atomNeurons.put(headLiteral2rules.r, headAtomNeuron);
        } else {
            headAtomNeuron.isShared = true;
            if (headLiteral2rules.s.entrySet().size() > 0) {
                WeightedNeuronMapping<AggregationNeuron> inputMapping;
                if ((inputMapping = (WeightedNeuronMapping<AggregationNeuron>) neuronMaps.extraInputMapping.get(headAtomNeuron)) != null) {    //if previously existing atom neuron already had input overmapping, create a new (incremental) one
                    neuronMaps.extraInputMapping.put(headAtomNeuron, new WeightedNeuronMapping<>(inputMapping));
                } else {
                    neuronMaps.extraInputMapping.put(headAtomNeuron, new WeightedNeuronMapping<>(headAtomNeuron.createWeightedInputs()));
                }
            }
        }
        for (Map.Entry<WeightedRule, LinkedHashSet<WeightedRule>> rules2groundings : headLiteral2rules.s.entrySet()) {
            boolean newAggNeuron = false;
            AggregationNeuron aggNeuron;
            if ((aggNeuron = neuronMaps.aggNeurons.get(rules2groundings.getKey())) == null) {
                newAggNeuron = true;
                aggNeuron = neuralBuilder.neuronFactory.createAggNeuron(rules2groundings.getKey());
                neuronMaps.aggNeurons.put(rules2groundings.getKey(), aggNeuron);
            } else {
                aggNeuron.isShared = true;
                if (rules2groundings.getValue().size() > 0) {
                    LinkedNeuronMapping<RuleNeuron> inputMapping;
                    if ((inputMapping = (LinkedNeuronMapping<RuleNeuron>) neuronMaps.extraInputMapping.get(aggNeuron)) != null) {    //if previously existing aggregation neuron already had input overmapping, create a new (incremental) one
                        neuronMaps.extraInputMapping.put(aggNeuron, new LinkedNeuronMapping<>(inputMapping));
                    } else {
                        neuronMaps.extraInputMapping.put(aggNeuron, new LinkedNeuronMapping<>(aggNeuron.getInputs()));
                    }
                }
            }
            if (newAtomNeuron) {
                headAtomNeuron.addInput(aggNeuron, rules2groundings.getKey().weight);
            } else {
                LOG.info("Warning-  modifying previous state - Creating input overmapping for this Atom neuron: " + headAtomNeuron);
                WeightedNeuronMapping<AggregationNeuron> inputMapping = (WeightedNeuronMapping<AggregationNeuron>) neuronMaps.extraInputMapping.get(headAtomNeuron);
                inputMapping.addLink(new Pair<>(aggNeuron, rules2groundings.getKey().weight));
            }
            for (WeightedRule grounding : rules2groundings.getValue()) {
                RuleNeurons ruleNeuron;
                if ((ruleNeuron = neuronMaps.ruleNeurons.get(grounding)) == null) {
                    if (grounding.hasWeightedBody()) {
                        ruleNeuron = neuralBuilder.neuronFactory.createWeightedRuleNeuron(grounding);
                    } else {
                        ruleNeuron = neuralBuilder.neuronFactory.createRuleNeuron(grounding);
                    }
                    neuronMaps.ruleNeurons.put(grounding, ruleNeuron);
                } else {
                    //ruleNeuron.isShared = true;
                    LOG.warning("Inconsistency - Specific rule neuron already contained in neuronmap!!");
                }
                if (newAggNeuron) {
                    aggNeuron.addInput(ruleNeuron);
                } else {
                    LOG.info("Warning-  modifying previous state - Creating input overmapping for this Agg neuron: " + aggNeuron);
                    LinkedNeuronMapping<RuleNeurons> inputMapping = (LinkedNeuronMapping<RuleNeurons>) neuronMaps.extraInputMapping.get(headAtomNeuron);
                    inputMapping.addLink(ruleNeuron);
                }
            }
        }

        return neuronMaps;
    }

    /**
     * Create FactNeurons mapped back to ground Literals
     *
     * @param groundFacts
     * @return
     */
    private Map<Literal, FactNeuron> neuronsFromFacts(Map<Literal, ValuedFact> groundFacts) {
        Map<Literal, FactNeuron> factNeurons = new HashMap<>();
        for (Map.Entry<Literal, ValuedFact> factEntry : groundFacts.entrySet()) {
            FactNeuron factNeuron = neuralBuilder.neuronFactory.createFactNeuron(factEntry.getValue());
            factNeurons.put(factEntry.getKey(), factNeuron);
        }
        return factNeurons;
    }

    /**
     * Given all existing neurons (either newly created or reused), connect RuleNeurons -> AtomNeurons (or FactNeurons).
     * Only newly created rule neurons, i.e. those that haven't been connected to their inputs yet, are processed.
     *
     * @return
     */
    @NotNull
    private DetailedNetwork connectRuleNeurons2atoms(GroundTemplate.NeuronMaps neuronMaps) {
        Set<NegationNeuron> negationNeurons = new HashSet<>();

        for (Map.Entry<WeightedRule, RuleNeurons> entry : neuronMaps.ruleNeurons.entrySet()) {
            RuleNeurons ruleNeuron = entry.getValue();
            if (ruleNeuron.inputCount() == entry.getKey().body.size()) {
                continue;   //this rule neuron is already connected (was created and taken from previous sample), connect only the newly created RuleNeurons
            }
            for (BodyAtom bodyAtom : entry.getKey().body) {
                Weight weight = bodyAtom.getConjunctWeight();

                AtomFact input = neuronMaps.atomNeurons.get(bodyAtom.getLiteral()); //input is an atom neuron?
                if (input == null) { //input is a fact neuron!
                    FactNeuron factNeuron = neuronMaps.factNeurons.get(bodyAtom.getLiteral());
                    if (factNeuron == null) {
                        LOG.severe("Error: no input found for this neuron!!: " + bodyAtom);
                    } else {
                        factNeuron.isShared = true;
                    }
                    input = factNeuron;
                    //weight = new Weight(new ScalarValue(settings.aggNeuronInputWeight));
                }
                if (bodyAtom.isNegated()) {
                    NegationNeuron negationNeuron = neuralBuilder.neuronFactory.createNegationNeuron(input, bodyAtom.getNegationActivation());
                    negationNeurons.add(negationNeuron);
                    input = negationNeuron;
                }
                if (ruleNeuron instanceof WeightedNeuron) {
                    ((WeightedNeuron) ruleNeuron).addInput(input, weight);
                } else {
                    ((RuleNeuron) ruleNeuron).addInput(input);
                }
            }
        }
        DetailedNetwork neuralNetwork = neuralBuilder.networkFactory.buildDetailedNetwork(neuronMaps, negationNeurons);
        return neuralNetwork;
    }
}