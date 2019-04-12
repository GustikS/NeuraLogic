package networks.structure.building;

import com.sun.istack.internal.NotNull;
import constructs.example.QueryAtom;
import constructs.template.components.GroundHeadRule;
import constructs.template.components.GroundRule;
import grounding.GroundTemplate;
import grounding.Grounder;
import grounding.GroundingSample;
import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.subsumption.Matching;
import networks.structure.building.builders.NeuralNetBuilder;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.QueryNeuron;
import networks.structure.components.neurons.types.AtomNeuron;
import networks.structure.components.types.DetailedNetwork;
import settings.Settings;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class responsible for building neural networks from GroundTemplates (ground rules and facts).
 * It resolves mostly from what rules, facts, and queries the networks should be created,
 * while the creation itself is outsourced to {@link NeuralNetBuilder}.
 *
 * @see NeuralNetBuilder
 */
public class Neuralizer {
    private static final Logger LOG = Logger.getLogger(Neuralizer.class.getName());
    private Settings settings;

    public NeuralNetBuilder neuralNetBuilder;

    public Neuralizer(Settings settings) {
        this.settings = settings;
        this.neuralNetBuilder = new NeuralNetBuilder(settings);
    }

    public Neuralizer(Grounder grounder) {
        this.settings = grounder.settings;
        this.neuralNetBuilder = new NeuralNetBuilder(settings);
        this.neuralNetBuilder.neuralBuilder.weightFactory = grounder.weightFactory;
    }

    /**
     * Turn GroundingSample, i.e. a set of ground rules and facts, into a NeuralProcessingSample, i.e. neural network.
     * <p>
     * A single grounding sample may produce multiple neural samples because the (lifted) query may be matched multiple times in the ground rules
     *
     * @param groundingSample
     * @return
     */
    public List<NeuralProcessingSample> neuralize(GroundingSample groundingSample) {
        neuralNetBuilder.setNeuronMaps(groundingSample.groundingWrap.getGroundTemplate().neuronMaps); //loading stored context from previous neural nets building

        List<QueryNeuron> queryNeurons = supervisedNeuralization(groundingSample);
        if (queryNeurons.isEmpty()) {
            LOG.warning("No inference network created for " + groundingSample.query);
        }

        groundingSample.groundingWrap.getGroundTemplate().neuronMaps = neuralNetBuilder.getNeuronMaps(); //storing the context back again

        List<NeuralProcessingSample> samples = queryNeurons.stream()
                .map(queryNeuron -> new NeuralProcessingSample(groundingSample.target, queryNeuron))
                .collect(Collectors.toList());
        return samples;
    }

    /**
     * Supervised network building (recursive network construction top-down from grounded rules)
     *
     * @return
     */
    private List<QueryNeuron> supervisedNeuralization(GroundingSample groundingSample) { // - todo test if all correct in sequential sharing mode!!!
        QueryAtom queryAtom = groundingSample.query;
        GroundTemplate groundTemplate = groundingSample.groundingWrap.getGroundTemplate();

        List<Literal> queryMatchingLiterals = getQueryMatchingLiterals(queryAtom, groundTemplate.groundRules);
        LOG.finest("QueryMatchingLiterals: "+ queryMatchingLiterals);

        DetailedNetwork neuralNetwork;
        if (settings.forceFullNetworks) {   //we can possibly still be forced to create the whole network, even if parts of it are not connected to the query, e.g. if the rules are not connected
            neuralNetwork = blindNeuralization(groundTemplate);
        } else {
            neuralNetBuilder = loadAllNeuronsStartingFromQueryLiterals(groundTemplate, queryMatchingLiterals);
            neuralNetBuilder.loadNeuronsFromFacts(groundTemplate.neuronMaps.groundFacts);
            LOG.fine("Neurons created: " + neuralNetBuilder.getNeuronMaps());
            neuralNetBuilder.connectAllNeurons();
            LOG.fine("All neurons connected.");
            neuralNetwork = neuralNetBuilder.finalizeStoredNetwork(groundTemplate.getId());
            LOG.fine("Neural network created: " + neuralNetwork);
        }

        return getQueryNeurons(queryAtom, neuralNetBuilder.getNeuronMaps(), neuralNetwork, queryMatchingLiterals);
    }

    /**
     * Unsupervised network building (flat network construction from all grounded rules)
     *
     * @param groundTemplate
     * @return
     */
    private DetailedNetwork blindNeuralization(GroundTemplate groundTemplate) {
        //simply create neurons for all the ground rules
        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>>> entry : groundTemplate.neuronMaps.groundRules.entrySet()) {
            neuralNetBuilder.loadNeuronsFromRules(entry.getKey(), entry.getValue());
        }
        groundTemplate.neuronMaps.groundRules.clear();   //remove rules that will have their neurons already created

        //and create facts
        neuralNetBuilder.loadNeuronsFromFacts(groundTemplate.neuronMaps.groundFacts);
        neuralNetBuilder.connectAllNeurons();
        DetailedNetwork detailedNetwork = neuralNetBuilder.finalizeStoredNetwork(groundTemplate.getId());
        return detailedNetwork;
    }

    /**
     * Recursively build the network top-down, taking only ground rules in support of the given literal into account
     *
     * @param literal
     * @param groundTemplate
     * @param closedSet
     */
    private void recursiveNeuronsCreation(@NotNull Literal literal, GroundTemplate groundTemplate, Set<Literal> closedSet) {
        if (closedSet.contains(literal)) {
            return;
        }
        closedSet.add(literal);

        LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>> ruleMap = groundTemplate.neuronMaps.groundRules.remove(literal);
        if (ruleMap != null) {
            neuralNetBuilder.loadNeuronsFromRules(literal, ruleMap);

            for (LinkedHashSet<GroundRule> groundings : ruleMap.values()) {
                for (GroundRule grounding : groundings) {
                    for (Literal bodyAtom : grounding.groundBody) {
                        recursiveNeuronsCreation(bodyAtom, groundTemplate, closedSet);
                    }
                }
            }
        }
    }

    @NotNull
    protected List<Literal> getQueryMatchingLiterals(QueryAtom queryAtom, @NotNull LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>>> groundRules) {

        Matching matching = new Matching();

        List<Literal> queryLiterals = new ArrayList<>();

        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>>> entry : groundRules.entrySet()) {  //find rules the head of which matches the query
            if (queryAtom.headAtom.literal.predicate().equals(entry.getKey().predicate()) && matching.subsumption(new Clause(queryAtom.headAtom.literal), new Clause(entry.getKey()))) { //todo check this method, also for speed
                queryLiterals.add(entry.getKey());
            }
        }
        return queryLiterals;
    }

    protected NeuralNetBuilder loadAllNeuronsStartingFromQueryLiterals(GroundTemplate groundTemplate, List<Literal> queryLiterals) {
        Set<Literal> closedSet = new HashSet<>();

        for (Literal queryLiteral : queryLiterals) {
            recursiveNeuronsCreation(queryLiteral, groundTemplate, closedSet);
            closedSet.add(queryLiteral);
        }

        return neuralNetBuilder;
    }

    @NotNull
    protected List<QueryNeuron> getQueryNeurons(QueryAtom queryAtom, NeuronMaps neuronMaps, NeuralNetwork neuralNetwork, List<Literal> queryMatchingLiterals) {
        List<QueryNeuron> queryNeurons = new ArrayList<>();
        for (Literal queryLiteral : queryMatchingLiterals) {
            AtomNeuron atomNeuron = neuronMaps.atomNeurons.get(queryLiteral);
            QueryNeuron queryNeuron = new QueryNeuron(queryAtom.ID, queryAtom.position, queryAtom.importance, atomNeuron, neuralNetwork);
            queryNeurons.add(queryNeuron);
        }
        return queryNeurons;
    }

}