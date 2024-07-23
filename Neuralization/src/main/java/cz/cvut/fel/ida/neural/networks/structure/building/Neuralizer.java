package cz.cvut.fel.ida.neural.networks.structure.building;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.LearningSample;
import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Constant;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.example.QueryAtom;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundHeadRule;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundRule;
import cz.cvut.fel.ida.logic.grounding.GroundTemplate;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralSets;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.generic.Timing;
import org.jetbrains.annotations.NotNull;

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
    public List<NeuralProcessingSample> neuralize(GroundTemplate groundTemplate, List<GroundingSample> samples) throws RuntimeException {
        timing.tic();
        networksCreated++;

        GroundingSample groundingSample = samples.get(0);
        NeuronMaps neuronMaps = (NeuronMaps) groundingSample.groundingWrap.getNeuronMaps();  //neuronmaps should be same for all samples
        if (neuronMaps == null) {
            neuronMaps = new NeuronMaps(groundingSample.groundingWrap.getGroundTemplate().groundRules, groundingSample.groundingWrap.getGroundTemplate().groundFacts);
            final NeuronMaps finalNeuronMaps = neuronMaps;
            samples.forEach(s -> s.groundingWrap.setNeuronMaps(finalNeuronMaps));
        }
        neuralNetBuilder.setNeuronMaps(neuronMaps); //loading stored context from previous neural nets building
        NeuralSets createdNeurons = new NeuralSets();    //a set of neurons used exclusively for this network being created only!

//        List<QueryNeuron> queryNeurons = supervisedNeuralization(groundingSample, currentNeuronSets);

        List<Literal> queryExpandedLiterals = new ArrayList<>();
        List<LogicSample> origSamples = new ArrayList<>();  //these two lists are aligned

        boolean noMatch = true;
        for (LogicSample sample : samples) {
            List<Literal> foundQueries = getQueryMatchingLiterals(sample.query, groundTemplate);
            if (foundQueries.isEmpty()) {
                String err = "Query [" + sample.query.headAtom + "] not matched anywhere in the template!";
                LOG.warning(err);
                if (sample.query.headAtom != null) {
                    queryExpandedLiterals.add(sample.query.headAtom.literal);   // we add it anyway not to lose the unentailed samples (they still can count e.g. towards accuracy)
                    origSamples.add(sample);
                }
            } else {
                noMatch = false;
                for (Literal foundQuery : foundQueries) {
                    if (foundQuery == null) {
                        throw new InputMismatchException("Null query matched for this sample: " + sample);
                    }
                    queryExpandedLiterals.add(foundQuery);
                    origSamples.add(sample);    //these two lists are aligned
                }
            }
        }
        if (noMatch) {
            if (groundingSample.query.headAtom == null) {
                DetailedNetwork neuralNetwork = blindNeuralization(groundingSample.groundingWrap.getGroundTemplate(), neuronMaps, createdNeurons);
                NeuralProcessingSample neuralProcessingSample = new NeuralProcessingSample(new ScalarValue(0), new QueryNeuron(groundingSample.getId(), 0, 0, null, neuralNetwork), groundingSample.type, settings);
                return Collections.singletonList(neuralProcessingSample);
            } else {
                String err = "Not a single query was matched anywhere in the template for any of: " + samples;
                LOG.severe(err);
//                throw new RuntimeException(err);
            }
        }

        DetailedNetwork neuralNetwork;
        if (settings.forceFullNetworks) {   //we can possibly still be forced to create the whole network, even if parts of it are not connected to the query, e.g. if the rules are not connected
            neuralNetwork = blindNeuralization(groundTemplate, neuronMaps, createdNeurons);
        } else {
            neuralNetBuilder = loadAllNeuronsStartingFromQueryLiterals(groundTemplate, queryExpandedLiterals, neuronMaps, createdNeurons);
            neuralNetwork = getDetailedNetwork(neuronMaps, createdNeurons, groundTemplate, queryExpandedLiterals);
        }

        List<NeuralProcessingSample> neuralSamples = new ArrayList<>();
        noMatch = true;
        for (int i = 0; i < queryExpandedLiterals.size(); i++) {
            LogicSample logicSample = origSamples.get(i);   //these two lists are aligned
            QueryAtom queryAtom = logicSample.query;
            AtomNeurons atomNeuron = neuralNetBuilder.getNeuronMaps().atomNeurons.get(queryExpandedLiterals.get(i));
            if (atomNeuron == null) {
                if (queryExpandedLiterals.size() <= 1) {
                    LOG.severe("No neural inference network created for " + queryAtom.toString());
                    throw new InputMismatchException("No inference network created for " + queryAtom);
                } else if (logicSample.target.greaterThan(Value.ZERO) && settings.trainOnlineResultsType != Settings.ResultsType.REGRESSION) {
                    LOG.warning("Unable to infer a positively labeled sample " + logicSample);
                }
            } else {
                noMatch = false;
            }
            QueryNeuron queryNeuron = new QueryNeuron(queryAtom.ID + ":" + queryAtom.headAtom.toString(), queryAtom.position, queryAtom.importance, atomNeuron, neuralNetwork);

            NeuralProcessingSample neuralProcessingSample = new NeuralProcessingSample(logicSample.target, queryNeuron, logicSample.type, settings);
            neuralSamples.add(neuralProcessingSample);
        }
        if (noMatch) {
            String err = "No neural inference network created for any query of the sample " + neuralSamples;
            LOG.severe(err);
//            throw new InputMismatchException(err);
        }

//        groundTemplate.neuronMaps = neuralNetBuilder.getNeuronMaps(); //storing the context back again

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
    public List<NeuralProcessingSample> neuralize(GroundingSample groundingSample) throws RuntimeException {
        timing.tic();
        networksCreated++;

        NeuronMaps neuronMaps = (NeuronMaps) groundingSample.groundingWrap.getNeuronMaps();
        if (neuronMaps == null) {
            neuronMaps = new NeuronMaps(groundingSample.groundingWrap.getGroundTemplate().groundRules, groundingSample.groundingWrap.getGroundTemplate().groundFacts);
            groundingSample.groundingWrap.setNeuronMaps(neuronMaps);
        }
        neuralNetBuilder.setNeuronMaps(neuronMaps); //loading stored context from previous neural nets building
        NeuralSets createdNeurons = new NeuralSets();    //a set of neurons used exclusively for this network being created only!

        if (groundingSample.query.headAtom == null) {
            DetailedNetwork neuralNetwork = blindNeuralization(groundingSample.groundingWrap.getGroundTemplate(), neuronMaps, createdNeurons);
            NeuralProcessingSample neuralProcessingSample = new NeuralProcessingSample(new ScalarValue(0), new QueryNeuron(groundingSample.getId(), 0, 0, null, neuralNetwork), groundingSample.type, settings);
            return Collections.singletonList(neuralProcessingSample);
        }

        List<QueryNeuron> queryNeurons = supervisedNeuralization(groundingSample, neuronMaps, createdNeurons);
        queryNeuronsCreated += queryNeurons.size();
        if (queryNeurons.isEmpty()) {
            LOG.severe("No inference network created for " + groundingSample.query);
        }

//        groundingSample.cache = neuralNetBuilder.getNeuronMaps(); //storing the context back again

        List<NeuralProcessingSample> samples = queryNeurons.stream().map(queryNeuron -> new NeuralProcessingSample(groundingSample.target, queryNeuron, groundingSample.type, settings)).collect(Collectors.toList());

        neuronCounts = createdNeurons.getCounts();
        timing.toc();
        return samples;
    }

    /**
     * Supervised network building (recursive network construction top-down from grounded rules)
     *
     * @return
     */
    private List<QueryNeuron> supervisedNeuralization(GroundingSample groundingSample, NeuronMaps neuronMaps, NeuralSets createdNeurons) throws RuntimeException { // - todo test if all correct in sequential sharing mode!!!
        QueryAtom queryAtom = groundingSample.query;
        GroundTemplate groundTemplate = groundingSample.groundingWrap.getGroundTemplate();

        List<Literal> queryMatchingLiterals = getQueryMatchingLiterals(queryAtom, groundTemplate);
        if (queryMatchingLiterals.isEmpty()) {
            String err = "Query [" + queryAtom.headAtom + "] not matched anywhere in the template. Cannot perform neural training.";
            LOG.severe(err);
            if (queryAtom.headAtom == null || !settings.queriesAlignedWithExamples) {
                return new ArrayList<>();   // if there are multiple queries per example, an unmatched query can be OK
            } else {
                throw new RuntimeException(err);
            }

        }
        LOG.finer("Obtained QueryMatchingLiterals: " + queryMatchingLiterals);

        DetailedNetwork neuralNetwork;
        if (settings.forceFullNetworks || queryMatchingLiterals.isEmpty()) {   //we can possibly still be forced to create the whole network, even if parts of it are not connected to the query, e.g. if the rules are not connected
            neuralNetwork = blindNeuralization(groundTemplate, neuronMaps, createdNeurons);
        } else {
            neuralNetBuilder = loadAllNeuronsStartingFromQueryLiterals(groundTemplate, queryMatchingLiterals, neuronMaps, createdNeurons);
            neuralNetwork = getDetailedNetwork(neuronMaps, createdNeurons, groundTemplate, queryMatchingLiterals);
        }

        return getQueryNeurons(queryAtom, neuralNetBuilder.getNeuronMaps(), neuralNetwork, queryMatchingLiterals);
    }


    /**
     * Unsupervised network building (flat network construction from all grounded rules)
     *
     * @param groundTemplate
     * @return
     */
    private DetailedNetwork blindNeuralization(GroundTemplate groundTemplate, NeuronMaps neuronMaps, NeuralSets currentNeuralSets) throws RuntimeException {
        //simply create neurons for all the ground rules
        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> entry : neuronMaps.groundRules.entrySet()) {
            neuralNetBuilder.loadNeuronsFromRules(entry.getKey(), entry.getValue(), currentNeuralSets);
        }
        neuronMaps.groundRules.clear();   //remove rules that will have their neurons already created

        return getDetailedNetwork(neuronMaps, currentNeuralSets, groundTemplate, null);
    }

    private DetailedNetwork getDetailedNetwork(NeuronMaps neuronMaps, NeuralSets createdNeurons, GroundTemplate groundTemplate, List<Literal> queryMatchingLiterals) throws RuntimeException {
        if (neuralNetBuilder.neuralBuilder.neuronFactory.neuronMaps.factNeurons.isEmpty() || settings.groundingMode == Settings.GroundingMode.SEQUENTIAL)
            neuralNetBuilder.loadNeuronsFromFacts(neuronMaps.groundFacts, createdNeurons);   //global sharing mode transfers facts   - todo check after changes

        LOG.fine("Neurons created: " + neuralNetBuilder.getNeuronMaps());
        neuralNetBuilder.connectAllNeurons(createdNeurons);
        LOG.fine("All neurons connected.");
        DetailedNetwork neuralNetwork = neuralNetBuilder.finalizeStoredNetwork(groundTemplate.getName(), createdNeurons, queryMatchingLiterals);
        LOG.fine("Final neural network created: " + neuralNetwork);
        return neuralNetwork;
    }

    /**
     * Recursively build the network top-down, taking only ground rules in support of the given literal into account
     *
     * @param literal
     * @param closedSet
     */
    private void recursiveNeuronsCreation(@NotNull Literal literal, Set<Literal> closedSet, NeuronMaps neuronMaps, NeuralSets currentNeuralSets, boolean splittable) {
        if (closedSet.contains(literal)) {
            return;
        }
        closedSet.add(literal);

        LinkedHashMap<GroundHeadRule, Collection<GroundRule>> ruleMap = neuronMaps.groundRules.remove(literal);
        if (ruleMap != null) {
            if (splittable) {
                neuralNetBuilder.loadSplittableNeuronsFromRules(literal, ruleMap, currentNeuralSets);
            } else {
                neuralNetBuilder.loadNeuronsFromRules(literal, ruleMap, currentNeuralSets);
            }

            groundRulesProcessed++;

            for (Map.Entry<GroundHeadRule, Collection<GroundRule>> entry : ruleMap.entrySet()) {
                Aggregation aggregation = entry.getKey().weightedRule.getAggregationFcn();

                if (!splittable && aggregation != null && aggregation.isSplittable()) { // Process masked literal
                    Literal maskedLiteral = entry.getKey().groundHead.maskTerms(aggregation.aggregableTerms());
                    recursiveNeuronsCreation(maskedLiteral, closedSet, neuronMaps, currentNeuralSets, true);
                    continue;
                }

                for (GroundRule grounding : entry.getValue()) {
                    for (Literal bodyAtom : grounding.groundBody) {
                        if (bodyAtom == null) {
                            throw new RuntimeException("Encoutered a null ground body atom in " + grounding);
                        }
                        recursiveNeuronsCreation(bodyAtom, closedSet, neuronMaps, currentNeuralSets, false);
                    }
                }
            }
        }
    }

    @NotNull
    protected List<Literal> getQueryMatchingLiterals(QueryAtom queryAtom, @NotNull GroundTemplate groundTemplate) {

        if (queryAtom.headAtom == null) {
            return new ArrayList<>(0);
        }

        // ground query (simple standard) - return if directly found as a head in some rule
        Literal queryLiteral = queryAtom.headAtom.literal;
        if (!queryLiteral.containsVariable()) {
            ArrayList<Literal> queries = new ArrayList<>();
            if (groundTemplate.groundRules.containsKey(queryLiteral)) {
                queries.add(queryLiteral);
            } else if (groundTemplate.groundFacts.containsKey(queryLiteral)) {
                LOG.severe("Quering directly facts with " + queryLiteral);
                queries.add(queryLiteral);
            }
            return queries;
        }

        //otherwise we need to check query via logical inference
        Matching matching = new Matching();
        List<Literal> queryLiterals = new ArrayList<>();

        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> entry : groundTemplate.groundRules.entrySet()) {  //find rules the head of which matches the query
            if (queryAtom.headAtom.literal.predicate().equals(entry.getKey().predicate()) && matching.subsumption(new Clause(queryAtom.headAtom.literal), new Clause(entry.getKey()))) { //todo check this method, also for speed
                queryLiterals.add(entry.getKey());
            }
        }
//        for (Map.Entry<Literal, ValuedFact> entry : groundTemplate.groundFacts.entrySet()) {
//            if (queryAtom.headAtom.literal.predicate().equals(entry.getKey().predicate()) && matching.subsumption(new Clause(queryAtom.headAtom.literal), new Clause(entry.getKey()))) { //todo check this method, also for speed
//                queryLiterals.add(entry.getKey());
//            }
//        }

        return queryLiterals;
    }

    protected NeuralNetBuilder loadAllNeuronsStartingFromQueryLiterals(GroundTemplate groundTemplate, List<Literal> queryLiterals, NeuronMaps neuronMaps, NeuralSets currentNeuralSets) {
        Set<Literal> closedSet = new HashSet<>();

        for (Literal queryLiteral : queryLiterals) {
            recursiveNeuronsCreation(queryLiteral, closedSet, neuronMaps, currentNeuralSets, false);
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

            QueryNeuron queryNeuron = new QueryNeuron(queryAtom.ID + ":" + queryAtom.headAtom.toString(), queryAtom.position, queryAtom.importance, atomNeuron, neuralNetwork);
            queryNeurons.add(queryNeuron);
        }
        return queryNeurons;
    }

}