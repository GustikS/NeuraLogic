package cz.cvut.fel.ida.neural.networks.structure.building;

import com.sun.istack.internal.NotNull;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.BodyAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundHeadRule;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundRule;
import cz.cvut.fel.ida.neural.networks.structure.building.builders.StatesBuilder;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralSets;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.AggregationState;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.*;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.LinkedMapping;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.NeuronMapping;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import cz.cvut.fel.ida.setup.Settings;

import java.util.*;
import java.util.logging.Logger;

public class NeuralNetBuilder {
    private static final Logger LOG = Logger.getLogger(NeuralNetBuilder.class.getName());

    /**
     * The single point of reference for creating anything neural from logic parts
     */
    public NeuralBuilder neuralBuilder;
    private Settings settings;

    public NeuralNetBuilder(Settings settings, NeuralBuilder neuralBuilder) {
        this.neuralBuilder = neuralBuilder;
        this.settings = settings;
    }

    public NeuralNetBuilder(Settings settings) {
        this.neuralBuilder = new NeuralBuilder(settings);
        this.settings = settings;
    }

    /**
     * Given a literal together with all corresponding rules where it appears as a head, create AtomNeuron -> AggNeurons -> RuleNeurons neurons and their connections.
     * Reuse existing neuronMaps, i.e., detect if a required neuron already exists in these,
     * and if so, then we need to add extra input mapping not to change the previously existing neurons, while still being able to reuse them.
     * <p>
     *
     * @param head  a head of all the subsequent rules
     * @param rules all the rules with all the groundings where it appears as a head
     */
    public void loadNeuronsFromRules(Literal head, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>> rules, NeuralSets createdNeurons) {
        NeuronMaps neuronMaps = neuralBuilder.neuronFactory.neuronMaps;

        boolean newAtomNeuron = false;
        boolean weightedAtomNeuron = false;

        AtomNeurons headAtomNeuron;

        //1) head AtomNeuron creation
        if ((headAtomNeuron = neuronMaps.atomNeurons.get(head)) == null) {
            newAtomNeuron = true;

            Iterator<Map.Entry<GroundHeadRule, LinkedHashSet<GroundRule>>> iterator = rules.entrySet().iterator();
            Map.Entry<GroundHeadRule, LinkedHashSet<GroundRule>> liftedRule = null;

            while (iterator.hasNext()) {
                liftedRule = iterator.next();
                if (!head.equals(liftedRule.getValue().iterator().next().groundHead)) {
                    LOG.severe("Ground heads corresponding to the same atom neuron are different!");
                }
                if (liftedRule.getKey().weightedRule.getWeight() != Weight.unitWeight) {
                    weightedAtomNeuron = true;
                }
            }
            if (weightedAtomNeuron) {
                headAtomNeuron = neuralBuilder.neuronFactory.createWeightedAtomNeuron(liftedRule.getKey().weightedRule.getHead(), head); //it doesn't matter which liftedRule's head (they are all the same)
                createdNeurons.weightedAtomNeurons.add((WeightedAtomNeuron) headAtomNeuron);
            } else {
                headAtomNeuron = neuralBuilder.neuronFactory.createUnweightedAtomNeuron(liftedRule.getKey().weightedRule.getHead(), head);
                createdNeurons.atomNeurons.add((AtomNeuron) headAtomNeuron);
            }
            if (headAtomNeuron.getComputationView(0).getAggregationState() instanceof AggregationState.CrossProducState) {   //the neuron will require complex input propagation
                neuronMaps.containsCrossproduct = true;
            }
        } else {
            headAtomNeuron.setShared(true);
            if (rules.entrySet().size() > 0) {  //if there are NEW rules for this headAtomNeuron to be processed, it means that we need to change its inputs in context of this new network!
                if (headAtomNeuron instanceof WeightedNeuron) {
//                    currentNeuronSets.weightedAtomNeurons.add((WeightedAtomNeuron) headAtomNeuron);
                    weightedAtomNeuron = true;
                    WeightedNeuronMapping<AggregationNeuron> inputMapping;
                    if ((inputMapping = (WeightedNeuronMapping<AggregationNeuron>) neuronMaps.extraInputMapping.get(headAtomNeuron)) != null) {    //if previously existing atom neuron already had input overmapping, create a new (incremental) one
                        neuronMaps.extraInputMapping.put(headAtomNeuron, new WeightedNeuronMapping<>(inputMapping));
                    } else {
                        neuronMaps.extraInputMapping.put(headAtomNeuron, new WeightedNeuronMapping<>(headAtomNeuron.getInputs(), ((WeightedNeuron) headAtomNeuron).getWeights()));
                    }
                } else {
//                    currentNeuronSets.atomNeurons.add((AtomNeuron) headAtomNeuron);
                    NeuronMapping<AggregationNeuron> inputMapping;
                    if ((inputMapping = (NeuronMapping<AggregationNeuron>) neuronMaps.extraInputMapping.get(headAtomNeuron)) != null) {    //if previously existing atom neuron already had input overmapping, create a new (incremental) one
                        neuronMaps.extraInputMapping.put(headAtomNeuron, new NeuronMapping<>(inputMapping));
                    } else {
                        neuronMaps.extraInputMapping.put(headAtomNeuron, new NeuronMapping<>(headAtomNeuron.getInputs()));
                    }
                }
            }
        }

        //2) AggregationNeurons creation
        for (Map.Entry<GroundHeadRule, LinkedHashSet<GroundRule>> rules2groundings : rules.entrySet()) {
            boolean newAggNeuron = false;
            AggregationNeuron aggNeuron;

            if ((aggNeuron = neuronMaps.aggNeurons.get(rules2groundings.getKey())) == null) {
                newAggNeuron = true;
                aggNeuron = neuralBuilder.neuronFactory.createAggNeuron(rules2groundings.getKey());
                if (aggNeuron.getComputationView(0).getAggregationState().getInputMask() != null) { // the neuron will require input masking!
                    neuronMaps.containsMasking = true;
                }
                createdNeurons.aggNeurons.add(aggNeuron);
            } else {
                aggNeuron.isShared = true;
                if (rules2groundings.getValue().size() > 0) {   //todo check
                    NeuronMapping<RuleNeuron> inputMapping;
                    if ((inputMapping = (NeuronMapping<RuleNeuron>) neuronMaps.extraInputMapping.get(aggNeuron)) != null) {    //if previously existing aggregation neuron already had input overmapping, create a new (incremental) one
                        neuronMaps.extraInputMapping.put(aggNeuron, new NeuronMapping<>(inputMapping));
                    } else {
                        neuronMaps.extraInputMapping.put(aggNeuron, new NeuronMapping<>(aggNeuron.getInputs()));
                    }
                }
            }
            if (newAtomNeuron) {
                if (weightedAtomNeuron) {
                    ((WeightedNeuron) headAtomNeuron).addInput(aggNeuron, rules2groundings.getKey().weightedRule.getWeight());
                } else {
                    headAtomNeuron.addInput(aggNeuron);
                }
            } else {
                LOG.info("Warning-  modifying previous state - Creating input overmapping for this Atom neuron: " + headAtomNeuron);
                WeightedNeuronMapping<AggregationNeuron> inputMapping = (WeightedNeuronMapping<AggregationNeuron>) neuronMaps.extraInputMapping.get(headAtomNeuron);
                inputMapping.addLink(aggNeuron);
                inputMapping.addWeight(rules2groundings.getKey().weightedRule.getWeight());
            }

            //3) RuleNeurons creation
            for (GroundRule grounding : rules2groundings.getValue()) {
                RuleNeurons ruleNeuron;

                if ((ruleNeuron = neuronMaps.ruleNeurons.get(grounding)) == null) {
                    if (grounding.weightedRule.detectWeights()) {
                        ruleNeuron = neuralBuilder.neuronFactory.createWeightedRuleNeuron(grounding);
                        createdNeurons.weightedRuleNeurons.add((WeightedRuleNeuron) ruleNeuron);
                    } else {
                        ruleNeuron = neuralBuilder.neuronFactory.createRuleNeuron(grounding);
                        createdNeurons.ruleNeurons.add((RuleNeuron) ruleNeuron);
                    }
                    if (ruleNeuron.getComputationView(0).getAggregationState() instanceof AggregationState.CrossProducState) {   //the neuron will require complex input propagation
                        neuronMaps.containsCrossproduct = true;
                    }
                } else {
                    //ruleNeuron.isShared = true;
                    LOG.severe("Inconsistency - Specific rule neuron already contained in neuronmap!! This should never happen...");
                }
                if (newAggNeuron) {
                    aggNeuron.addInput(ruleNeuron);
                } else {
                    LOG.info("Warning-  modifying previous state - Creating input overmapping for this Agg neuron: " + aggNeuron);
                    NeuronMapping<RuleNeurons> inputMapping = (NeuronMapping<RuleNeurons>) neuronMaps.extraInputMapping.get(headAtomNeuron);
                    inputMapping.addLink(ruleNeuron);
                }
            }
        }
    }

    /**
     * Create FactNeurons mapped back to ground Literals
     * <p>
     * Remove fact neurons from neuronmaps that are never used? (probably unnecessary, they wont appear in the final network anyway)
     * No, keep them, as they are a valid part of the network (we can possibly query them).
     *
     * @param groundFacts
     * @param createdNeurons
     * @return
     */
    public void loadNeuronsFromFacts(Map<Literal, ValuedFact> groundFacts, NeuralSets createdNeurons) {
        for (Map.Entry<Literal, ValuedFact> factEntry : groundFacts.entrySet()) {
            neuralBuilder.neuronFactory.createFactNeuron(factEntry.getValue());
        }
        createdNeurons.factNeurons.addAll(neuralBuilder.neuronFactory.neuronMaps.factNeurons.values());
        groundFacts.clear();  //remove facts that will already have corresponding neurons with them
    }

    /**
     * Given all existing neurons (either newly created or reused), connect RuleNeurons -> AtomNeurons (or FactNeurons).
     * Only newly created rule neurons, i.e. those that haven't been connected to their inputs yet, are processed.
     *
     * @return
     */
    @NotNull
    public void connectAllNeurons(NeuralSets createdNeurons) {
        NeuronMaps neuronMaps = neuralBuilder.neuronFactory.neuronMaps;

        for (Map.Entry<GroundRule, RuleNeurons> entry : neuronMaps.ruleNeurons.entrySet()) {    //todo iterate only newly created from currentNeuronSets here
            RuleNeurons ruleNeuron = entry.getValue();
            if (ruleNeuron.inputCount() == entry.getKey().weightedRule.getBody().size()) {
                continue;   //this rule neuron is already connected (was created and taken from previous sample), connect only the newly created RuleNeurons
            }

            for (int i = 0; i < entry.getKey().groundBody.length; i++) {
                BodyAtom liftedBodyAtom = entry.getKey().weightedRule.getBody().get(i);
                Literal literal = entry.getKey().groundBody[i];

                Weight weight = liftedBodyAtom.getConjunctWeight();

                AtomFact input = neuronMaps.atomNeurons.get(literal); //input is an atom neuron?
                if (input == null) { //input is a fact neuron!
                    FactNeuron factNeuron = neuronMaps.factNeurons.get(literal);
                    if (factNeuron == null) {
                        LOG.severe("Error: no input found for this neuron!!: " + literal);
                    } else {
                        //factNeuron.isShared = true; //they might not be shared as all the fact neurons are created in advance
                    }
                    input = factNeuron;
//                    currentNeuronSets.factNeurons.add(factNeuron);
                }
                if (liftedBodyAtom.isNegated()) {
                    NegationNeuron negationNeuron = neuralBuilder.neuronFactory.createNegationNeuron(input, liftedBodyAtom.getNegationActivation());
                    input = negationNeuron;
//                    currentNeuronSets.negationNeurons.add(negationNeuron);
                }
                if (ruleNeuron instanceof WeightedNeuron) {
                    ((WeightedNeuron) ruleNeuron).addInput(input, weight);
                } else {
                    ((RuleNeuron) ruleNeuron).addInput(input);
                }
            }
        }
    }

    /**
     * This is only meant to go through the most necessary postprocessing steps to make for a valid neural network.
     * For the more advanced postprocessing optimization there is a whole configurable pipeline in {NeuralNetsBuilder}
     *
     * @return
     */
    public DetailedNetwork finalizeStoredNetwork(String id, NeuralSets createdNeurons, List<Literal> queryMatchingLiterals) throws RuntimeException {
        List<AtomNeurons> queryNeurons = null;
        if (queryMatchingLiterals != null) {

            queryNeurons = new ArrayList<>();
            for (Literal queryMatchingLiteral : queryMatchingLiterals) {
                AtomNeurons qn = neuralBuilder.neuronFactory.neuronMaps.atomNeurons.get(queryMatchingLiteral);
                if (qn == null){
                    String err = "Query: " + queryMatchingLiteral + " was not matched anywhere in the ground network - Cannot calculate its output!";
                    LOG.severe(err);
                    LOG.warning(" -> This most likely means that the template is wrong as there is no proof-path from the example to the query");
                    LOG.warning("   -> Check all the predicate signatures etc. to make sure the template matches your examples and that there is at least 1 inference chain to the query");
//                    System.exit(5);
                    throw new InputMismatchException(err);
                }
                queryNeurons.add(qn);
            }

//            queryNeurons = queryMatchingLiterals.stream().map(key -> {
//                AtomNeurons qn = neuralBuilder.neuronFactory.neuronMaps.atomNeurons.get(key);
//                if (qn == null){
//                    LOG.severe("Query not matched anywhere in the ground network: " + key);
//                    LOG.severe("Cannot calculate its output!");
//                    System.exit(5);
////                    throw new Exception("Query not matched anywhere in the ground network:");
//                }
//                return qn;
//            }).collect(Collectors.toList());
        }
        DetailedNetwork neuralNetwork = neuralBuilder.networkFactory.createDetailedNetwork(queryNeurons, createdNeurons, id, neuralBuilder.neuronFactory.neuronMaps.extraInputMapping);
        LOG.fine("DetailedNetwork created.");

        StatesBuilder statesBuilder = neuralBuilder.statesBuilder;
        //fill all the states with correct dimension values
        statesBuilder.inferValues(neuralNetwork);   // todo somehow iterate only over the created neurons (but in topological order) - is that faster?

        LOG.fine("Neuron dimensions inferred.");

        if (settings.dropoutRate > 0) {
            statesBuilder.setupDropoutStates(neuralNetwork);  //setup individual dropout rates for each neuron
        }

        if (getNeuronMaps().containsCrossproduct) {
            neuralNetwork.containsCrossProducts = true;
        }
        if (getNeuronMaps().containsMasking) {
            neuralNetwork.containsInputMasking = true;
        }

        //if there are input overmappings, create appropriate states for them to be later stored in neural cache
        if (neuralNetwork.extraInputMapping != null && !neuralNetwork.extraInputMapping.isEmpty()) {
            statesBuilder.addLinkedInputsToNetworkStates(neuralNetwork);
        }

        //if there is the need, check parentCounts and store them by the network if needed
        if (settings.parentCounting || settings.neuralNetsPostProcessing) {
            neuralNetwork.outputMapping = calculateOutputs(neuralNetwork);
            if (settings.parentCounting)
                statesBuilder.setupParentStateNumbers(neuralNetwork);
        }

        if (settings.parallelTraining) {
            int sharedNeuronsCount = statesBuilder.makeSharedStatesRecursively(neuralNetwork);
            LOG.fine("Shared neurons marked.");
            neuralNetwork.setSharedNeuronsCount(sharedNeuronsCount);
        }

        return neuralNetwork;
    }


    public Map<BaseNeuron, LinkedMapping> calculateOutputs(TopologicNetwork<State.Neural.Structure> network) {
        Map<BaseNeuron, LinkedMapping> outputMapping = new HashMap<>();

        for (BaseNeuron parent : network.allNeuronsTopologic) {
            Iterator<BaseNeuron> inputs = network.getInputs(parent);
            BaseNeuron child;
            while (inputs.hasNext() && (child = inputs.next()) != null) {
                LinkedMapping parentMapping = outputMapping.computeIfAbsent(child, f -> new NeuronMapping());
                parentMapping.addLink(parent);
            }
        }
        return outputMapping;
    }


    public NeuronMaps getNeuronMaps() {
        return neuralBuilder.neuronFactory.neuronMaps;
    }

    public void setNeuronMaps(NeuronMaps neuronMaps) {
        this.neuralBuilder.neuronFactory.neuronMaps = neuronMaps;
    }
}
