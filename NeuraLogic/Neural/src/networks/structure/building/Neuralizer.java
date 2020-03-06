package networks.structure.building;

import com.sun.istack.internal.NotNull;
import constructs.building.factories.WeightFactory;
import constructs.example.LogicSample;
import constructs.example.QueryAtom;
import constructs.template.components.GroundHeadRule;
import constructs.template.components.GroundRule;
import exporting.Exportable;
import grounding.GroundTemplate;
import grounding.GroundingSample;
import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.subsumption.Matching;
import networks.structure.building.builders.NeuralNetBuilder;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.NeuralSets;
import networks.structure.components.neurons.QueryNeuron;
import networks.structure.components.neurons.types.AtomNeurons;
import networks.structure.components.types.DetailedNetwork;
import settings.Settings;
import utils.Timing;

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
public class Neuralizer implements Exportable {
    private static final Logger LOG = Logger.getLogger(Neuralizer.class.getName());
    transient private Settings settings;

    transient public NeuralNetBuilder neuralNetBuilder;


    public NeuralSets.NeuronCounter neuronCounts;
    int queryNeuronsCreated;
    int groundRulesProcessed;
    int networksCreated;

    public Timing timing;

    public Neuralizer(Settings settings) {
        this.settings = settings;
        this.neuralNetBuilder = new NeuralNetBuilder(settings);
        this.timing = new Timing();
    }

    public Neuralizer(Settings settings, WeightFactory weightFactory) {
        this(settings);
        this.neuralNetBuilder.neuralBuilder.weightFactory = weightFactory;
    }

    /**
     * KB mode - neuralize everything at once with multiple different queries
     *
     * @param groundTemplate
     * @param samples
     * @return
     */
    public List<NeuralProcessingSample> neuralize(GroundTemplate groundTemplate, List<? extends LogicSample> samples) {
        timing.tic();
        networksCreated++;

        neuralNetBuilder.setNeuronMaps(groundTemplate.neuronMaps); //loading stored context from previous neural nets building
        NeuralSets createdNeurons = new NeuralSets();    //a set of neurons used exclusively for this network being created only!

//        List<QueryNeuron> queryNeurons = supervisedNeuralization(groundingSample, currentNeuronSets);

        List<Literal> queryMatchingLiterals = new ArrayList<>();
        List<LogicSample> origSamples = new ArrayList<>();  //these two lists are aligned

        for (LogicSample sample : samples) {
            List<Literal> foundQueries = getQueryMatchingLiterals(sample.query, groundTemplate.groundRules);
            for (Literal foundQuery : foundQueries) {
                queryMatchingLiterals.add(foundQuery);
                origSamples.add(sample);    //these two lists are aligned
            }
        }

        DetailedNetwork neuralNetwork;
        if (settings.forceFullNetworks) {   //we can possibly still be forced to create the whole network, even if parts of it are not connected to the query, e.g. if the rules are not connected
            neuralNetwork = blindNeuralization(groundTemplate, createdNeurons);
        } else {
            neuralNetBuilder = loadAllNeuronsStartingFromQueryLiterals(groundTemplate, queryMatchingLiterals, createdNeurons);

            neuralNetwork = getDetailedNetwork(createdNeurons, groundTemplate, queryMatchingLiterals);
        }

        List<NeuralProcessingSample> neuralSamples = new ArrayList<>();
        for (int i = 0; i < queryMatchingLiterals.size(); i++) {
            LogicSample logicSample = origSamples.get(i);
            QueryAtom queryAtom = logicSample.query;
            AtomNeurons atomNeuron = neuralNetBuilder.getNeuronMaps().atomNeurons.get(queryMatchingLiterals.get(i));
            if (atomNeuron == null) {
                LOG.severe("No inference network created for " + queryAtom);
            }
            QueryNeuron queryNeuron = new QueryNeuron(queryAtom.ID, queryAtom.position, queryAtom.importance, atomNeuron, neuralNetwork);

            NeuralProcessingSample neuralProcessingSample = new NeuralProcessingSample(logicSample.target, queryNeuron);
            neuralSamples.add(neuralProcessingSample);
        }

        groundTemplate.neuronMaps = neuralNetBuilder.getNeuronMaps(); //storing the context back again

        neuronCounts = createdNeurons.getCounts();
        timing.toc();
        return neuralSamples;
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
        timing.tic();
        networksCreated++;

        neuralNetBuilder.setNeuronMaps(groundingSample.groundingWrap.getGroundTemplate().neuronMaps); //loading stored context from previous neural nets building
        NeuralSets createdNeurons = new NeuralSets();    //a set of neurons used exclusively for this network being created only!

        List<QueryNeuron> queryNeurons = supervisedNeuralization(groundingSample, createdNeurons);
        queryNeuronsCreated += queryNeurons.size();
        if (queryNeurons.isEmpty()) {
            LOG.severe("No inference network created for " + groundingSample.query);
        }

        groundingSample.groundingWrap.getGroundTemplate().neuronMaps = neuralNetBuilder.getNeuronMaps(); //storing the context back again

        List<NeuralProcessingSample> samples = queryNeurons.stream()
                .map(queryNeuron -> new NeuralProcessingSample(groundingSample.target, queryNeuron))
                .collect(Collectors.toList());

        neuronCounts = createdNeurons.getCounts();
        timing.toc();
        return samples;
    }

    /**
     * Supervised network building (recursive network construction top-down from grounded rules)
     *
     * @return
     */
    private List<QueryNeuron> supervisedNeuralization(GroundingSample groundingSample, NeuralSets createdNeurons) { // - todo test if all correct in sequential sharing mode!!!
        QueryAtom queryAtom = groundingSample.query;
        GroundTemplate groundTemplate = groundingSample.groundingWrap.getGroundTemplate();

        List<Literal> queryMatchingLiterals = getQueryMatchingLiterals(queryAtom, groundTemplate.groundRules);
        if (queryMatchingLiterals.isEmpty()) {
            LOG.severe("Query not matched anywhere in the template:" + queryAtom);
            System.exit(5);
        }
        LOG.finer("Obtained QueryMatchingLiterals: " + queryMatchingLiterals);

        DetailedNetwork neuralNetwork;
        if (settings.forceFullNetworks) {   //we can possibly still be forced to create the whole network, even if parts of it are not connected to the query, e.g. if the rules are not connected
            neuralNetwork = blindNeuralization(groundTemplate, createdNeurons);
        } else {
            neuralNetBuilder = loadAllNeuronsStartingFromQueryLiterals(groundTemplate, queryMatchingLiterals, createdNeurons);
            neuralNetwork = getDetailedNetwork(createdNeurons, groundTemplate, queryMatchingLiterals);
        }

        return getQueryNeurons(queryAtom, neuralNetBuilder.getNeuronMaps(), neuralNetwork, queryMatchingLiterals);
    }


    /**
     * Unsupervised network building (flat network construction from all grounded rules)
     *
     * @param groundTemplate
     * @return
     */
    private DetailedNetwork blindNeuralization(GroundTemplate groundTemplate, NeuralSets currentNeuralSets) {
        //simply create neurons for all the ground rules
        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>>> entry : groundTemplate.neuronMaps.groundRules.entrySet()) {
            neuralNetBuilder.loadNeuronsFromRules(entry.getKey(), entry.getValue(), currentNeuralSets);
        }
        groundTemplate.neuronMaps.groundRules.clear();   //remove rules that will have their neurons already created

        return getDetailedNetwork(currentNeuralSets, groundTemplate, null);
    }

    private DetailedNetwork getDetailedNetwork(NeuralSets createdNeurons, GroundTemplate groundTemplate, List<Literal> queryMatchingLiterals) {
        if (neuralNetBuilder.neuralBuilder.neuronFactory.neuronMaps.factNeurons.isEmpty() || settings.groundingMode == Settings.GroundingMode.SEQUENTIAL)
            neuralNetBuilder.loadNeuronsFromFacts(groundTemplate.neuronMaps.groundFacts, createdNeurons);   //global sharing mode transfers facts   - todo check after changes

        LOG.fine("Neurons created: " + neuralNetBuilder.getNeuronMaps());
        neuralNetBuilder.connectAllNeurons(createdNeurons);
        LOG.fine("All neurons connected.");
        DetailedNetwork neuralNetwork = neuralNetBuilder.finalizeStoredNetwork(groundTemplate.getId(), createdNeurons, queryMatchingLiterals);
        LOG.fine("Final neural network created: " + neuralNetwork);
        return neuralNetwork;
    }

    /**
     * Recursively build the network top-down, taking only ground rules in support of the given literal into account
     *
     * @param literal
     * @param groundTemplate
     * @param closedSet
     */
    private void recursiveNeuronsCreation(@NotNull Literal literal, GroundTemplate groundTemplate, Set<Literal> closedSet, NeuralSets currentNeuralSets) {
        if (closedSet.contains(literal)) {
            return;
        }
        closedSet.add(literal);

        LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>> ruleMap = groundTemplate.neuronMaps.groundRules.remove(literal);
        if (ruleMap != null) {
            neuralNetBuilder.loadNeuronsFromRules(literal, ruleMap, currentNeuralSets);
            groundRulesProcessed++;

            for (LinkedHashSet<GroundRule> groundings : ruleMap.values()) {
                for (GroundRule grounding : groundings) {
                    for (Literal bodyAtom : grounding.groundBody) {
                        recursiveNeuronsCreation(bodyAtom, groundTemplate, closedSet, currentNeuralSets);
                    }
                }
            }
        }
    }

    @NotNull
    protected List<Literal> getQueryMatchingLiterals(QueryAtom queryAtom, @NotNull LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>>> groundRules) {

        // ground query (simple, standard)?
        if (!queryAtom.headAtom.literal.containsVariable()) {
            ArrayList<Literal> queries = new ArrayList<>();
            queries.add(queryAtom.headAtom.literal);
            return queries;
        }

        //otherwise we need to check query via logical inference
        Matching matching = new Matching();
        List<Literal> queryLiterals = new ArrayList<>();

        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>>> entry : groundRules.entrySet()) {  //find rules the head of which matches the query
            if (queryAtom.headAtom.literal.predicate().equals(entry.getKey().predicate()) && matching.subsumption(new Clause(queryAtom.headAtom.literal), new Clause(entry.getKey()))) { //todo check this method, also for speed
                queryLiterals.add(entry.getKey());
            }
        }
        return queryLiterals;
    }

    protected NeuralNetBuilder loadAllNeuronsStartingFromQueryLiterals(GroundTemplate groundTemplate, List<Literal> queryLiterals, NeuralSets currentNeuralSets) {
        Set<Literal> closedSet = new HashSet<>();

        for (Literal queryLiteral : queryLiterals) {
            recursiveNeuronsCreation(queryLiteral, groundTemplate, closedSet, currentNeuralSets);
            closedSet.add(queryLiteral);
        }

        return neuralNetBuilder;
    }

    @NotNull
    protected List<QueryNeuron> getQueryNeurons(QueryAtom queryAtom, NeuronMaps neuronMaps, NeuralNetwork neuralNetwork, List<Literal> queryMatchingLiterals) {
        List<QueryNeuron> queryNeurons = new ArrayList<>();
        for (Literal queryLiteral : queryMatchingLiterals) {
            AtomNeurons atomNeuron = neuronMaps.atomNeurons.get(queryLiteral);
            if (atomNeuron == null) {
                LOG.severe("Query not matched!");
            }
            QueryNeuron queryNeuron = new QueryNeuron(queryAtom.ID, queryAtom.position, queryAtom.importance, atomNeuron, neuralNetwork);
            queryNeurons.add(queryNeuron);
        }
        return queryNeurons;
    }

}