package networks.structure.building;

import constructs.example.QueryAtom;
import constructs.template.components.BodyAtom;
import constructs.template.components.WeightedRule;
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
import org.jetbrains.annotations.NotNull;
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
        neuralNetBuilder.setNeuronMaps(groundingSample.grounding.getGroundTemplate().neuronMaps); //loading stored context from previous neural nets building

        List<QueryNeuron> queryNeurons = supervisedNeuralization(groundingSample.query, groundingSample.grounding.getGroundTemplate());

        groundingSample.grounding.getGroundTemplate().neuronMaps = neuralNetBuilder.getNeuronMaps(); //storing the context back again

        return queryNeurons.stream()
                .map(queryNeuron -> new NeuralProcessingSample(groundingSample.target, queryNeuron))
                .collect(Collectors.toList());
    }

    /**
     * Supervised network building (recursive network construction top-down from grounded rules)
     *
     * @param queryAtom
     * @return
     */
    private List<QueryNeuron> supervisedNeuralization(QueryAtom queryAtom, GroundTemplate groundTemplate) { // - todo test if all correct in sequential sharing mode!!!

        List<Literal> queryMatchingLiterals = getQueryMatchingLiterals(queryAtom, groundTemplate.groundRules);

        DetailedNetwork neuralNetwork;
        if (settings.forceFullNetworks) {   //we can possibly still be forced to create the whole network, even if parts of it are useless for the query
            neuralNetwork = blindNeuralization(groundTemplate);
        } else {
            neuralNetBuilder = loadAllNeuronsStartingFromQueryLiterals(groundTemplate, queryMatchingLiterals);
            neuralNetBuilder.loadNeuronsFromFacts(groundTemplate.groundFacts);
            neuralNetBuilder.connectAllNeurons();
            neuralNetwork = neuralNetBuilder.finalizeStoredNetwork(groundTemplate.getId());
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
        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> entry : groundTemplate.groundRules.entrySet()) {
            neuralNetBuilder.loadNeuronsFromRules(entry.getKey(), entry.getValue());
        }
        //and facts
        neuralNetBuilder.loadNeuronsFromFacts(groundTemplate.groundFacts);
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

        LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>> ruleMap = groundTemplate.groundRules.get(literal);
        neuralNetBuilder.loadNeuronsFromRules(literal, ruleMap);

        for (LinkedHashSet<WeightedRule> groundings : ruleMap.values()) {
            for (WeightedRule grounding : groundings) {
                for (BodyAtom bodyAtom : grounding.body) {
                    recursiveNeuronsCreation(bodyAtom.literal, groundTemplate, closedSet);
                }
            }
        }
    }

    @NotNull
    protected List<Literal> getQueryMatchingLiterals(QueryAtom queryAtom, @NotNull LinkedHashMap<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> groundRules) {

        Matching matching = new Matching();

        List<Literal> queryLiterals = new ArrayList<>();

        for (Map.Entry<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> entry : groundRules.entrySet()) {  //find rules the head of which matches the query
            if (queryAtom.headAtom.literal.predicate().equals(entry.getKey().predicate()) && matching.subsumption(new Clause(queryAtom.headAtom.literal), new Clause(entry.getKey()))) { //todo check this method, also for speed
                queryLiterals.add(entry.getKey());
            }
        }
        return queryLiterals;
    }

    protected NeuralNetBuilder loadAllNeuronsStartingFromQueryLiterals(GroundTemplate groundTemplate, List<Literal> queryLiterals) {
        Set<Literal> closedSet = new HashSet<>();

        for (Literal queryLiteral : queryLiterals) {
            closedSet.add(queryLiteral);
            recursiveNeuronsCreation(queryLiteral, groundTemplate, closedSet);
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