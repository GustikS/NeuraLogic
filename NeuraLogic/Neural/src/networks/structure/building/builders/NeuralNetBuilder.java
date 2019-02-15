package networks.structure.building.builders;

import constructs.example.ValuedFact;
import constructs.template.components.BodyAtom;
import constructs.template.components.WeightedRule;
import ida.ilp.logic.Literal;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.computation.iteration.visitors.states.networks.ParentsTransfer;
import networks.structure.building.NeuronMaps;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.neurons.types.*;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.inputMappings.LinkedMapping;
import networks.structure.metadata.inputMappings.NeuronMapping;
import networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import networks.structure.metadata.states.State;
import networks.structure.metadata.states.States;
import networks.structure.metadata.states.StatesCache;
import org.jetbrains.annotations.NotNull;
import settings.Settings;

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
        this.neuralBuilder = new NeuralBuilder();
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
    public void loadNeuronsFromRules(Literal head, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>> rules) {
        NeuronMaps neuronMaps = neuralBuilder.neuronFactory.neuronMaps;

        boolean newAtomNeuron = false;
        AtomNeuron headAtomNeuron;

        //1) head AtomNeuron creation
        if ((headAtomNeuron = neuronMaps.atomNeurons.get(head)) == null) {
            newAtomNeuron = true;
            headAtomNeuron = neuralBuilder.neuronFactory.createAtomNeuron(rules.entrySet().iterator().next().getValue().iterator().next().head); //it doesn't matter which rule's head (they are all the same)
        } else {
            headAtomNeuron.isShared = true;
            if (rules.entrySet().size() > 0) {  //if there are NEW rules for this headAtomNeuron to be processed, it means that we need to change its inputs in context of this new network! todo check for normal mode if really only the new rules are passed in
                WeightedNeuronMapping<AggregationNeuron> inputMapping;
                if ((inputMapping = (WeightedNeuronMapping<AggregationNeuron>) neuronMaps.extraInputMapping.get(headAtomNeuron)) != null) {    //if previously existing atom neuron already had input overmapping, create a new (incremental) one
                    neuronMaps.extraInputMapping.put(headAtomNeuron, new WeightedNeuronMapping<>(inputMapping));
                } else {
                    neuronMaps.extraInputMapping.put(headAtomNeuron, new WeightedNeuronMapping<>(headAtomNeuron.getInputs(), headAtomNeuron.getWeights()));
                }
            }
        }
        //2) AggregationNeurons creation
        for (Map.Entry<WeightedRule, LinkedHashSet<WeightedRule>> rules2groundings : rules.entrySet()) {
            boolean newAggNeuron = false;
            AggregationNeuron aggNeuron;

            if ((aggNeuron = neuronMaps.aggNeurons.get(rules2groundings.getKey())) == null) {
                newAggNeuron = true;
                aggNeuron = neuralBuilder.neuronFactory.createAggNeuron(rules2groundings.getKey());
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
                headAtomNeuron.addInput(aggNeuron, rules2groundings.getKey().weight);
            } else {
                LOG.info("Warning-  modifying previous state - Creating input overmapping for this Atom neuron: " + headAtomNeuron);
                WeightedNeuronMapping<AggregationNeuron> inputMapping = (WeightedNeuronMapping<AggregationNeuron>) neuronMaps.extraInputMapping.get(headAtomNeuron);
                inputMapping.addLink(aggNeuron);
                inputMapping.addWeight(rules2groundings.getKey().weight);
            }
            //3) RuleNeurons creation
            for (WeightedRule grounding : rules2groundings.getValue()) {
                RuleNeurons ruleNeuron;

                if ((ruleNeuron = neuronMaps.ruleNeurons.get(grounding)) == null) {
                    if (grounding.hasWeightedBody()) {
                        ruleNeuron = neuralBuilder.neuronFactory.createWeightedRuleNeuron(grounding);
                    } else {
                        ruleNeuron = neuralBuilder.neuronFactory.createRuleNeuron(grounding);
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
     * @return
     */
    public void loadNeuronsFromFacts(Map<Literal, ValuedFact> groundFacts) {
        for (Map.Entry<Literal, ValuedFact> factEntry : groundFacts.entrySet()) {
            neuralBuilder.neuronFactory.createFactNeuron(factEntry.getValue());
        }
    }

    /**
     * Given all existing neurons (either newly created or reused), connect RuleNeurons -> AtomNeurons (or FactNeurons).
     * Only newly created rule neurons, i.e. those that haven't been connected to their inputs yet, are processed.
     *
     * @return
     */
    @NotNull
    public void connectAllNeurons() {
        NeuronMaps neuronMaps = neuralBuilder.neuronFactory.neuronMaps;

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
                    input = negationNeuron;
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
     * For the more advanced postprocessing optimization there is a whole configurable pipeline in {@link pipelines.building.NeuralNetsBuilder}
     *
     * @param id
     * @return
     */
    public DetailedNetwork finalizeStoredNetwork(String id) {
        DetailedNetwork neuralNetwork = neuralBuilder.networkFactory.createDetailedNetwork(neuralBuilder.neuronFactory.neuronMaps, id);

        //fill all the states with correct dimension values
        createValues(neuralNetwork);

        if (settings.dropoutRate > 0) {
            setupDropoutStates(neuralNetwork);  //setup individual dropout rates for each neuron
        }

        //if there is the need, create states cache in the neural network
        if (!neuralNetwork.extraInputMapping.isEmpty())
            setupNeuronStatesCache(neuralNetwork, getStates(neuralNetwork, settings), getStatesInitializer(settings));

        if (settings.parentCounting) {
            neuralNetwork.outputMapping = calculateOutputs(neuralNetwork);
            setupParentStateNumbers(neuralNetwork);
        }

        int sharedNeuronsCount = makeSharedNeuronsRecursively(neuralNetwork);

        neuralNetwork.hasSharedNeurons = sharedNeuronsCount > 0;
        neuralNetwork.sharedNeuronsCount = sharedNeuronsCount;

        return neuralNetwork;
    }

    /**
     * Infer correct dimensions of all the value tensors within this network and create respective {@link networks.computation.evaluation.values.Value} objects.
     *
     * @param neuralNetwork
     */
    private void createValues(DetailedNetwork neuralNetwork) {
        //todo next
    }

    private void setupDropoutStates(DetailedNetwork neuralNetwork) {
        //todo next
    }

    /**
     * Here we use the current network's input mapping to go trough all the neurons recursively.
     * Although it is not necessary that all the neurons marked as such will be actually shared, as those input
     * mappings added by this network and used by only this network will get marked as shared too, but it is much easier
     * than remembering for each neuron what was the last network that added inputs to it - which would be the actual network
     * to use the input mappings from for sharing flags marking, because that must be the one the inputs of which for the
     * respective neuron should be used, because those are surely all shared.
     * I.e. just exclude inputs possibly added by this network.
     * todo improve by adding the pointer to the inputmapping for the network - if it is equal to this one, do not consider the last input list in inputovermaps
     *
     * @param detailedNetwork
     * @return
     */
    private int makeSharedNeuronsRecursively(DetailedNetwork<State.Neural.Structure> detailedNetwork) {
        int sharedCount = 0;
        for (int i = detailedNetwork.allNeuronsTopologic.size() - 1; i > 0; i--) {
            BaseNeuron<Neuron, State.Neural> neuron = detailedNetwork.allNeuronsTopologic.get(i);
            if (neuron.isShared) {
                sharedCount++;
                neuron.makeShared(settings);
                Iterator<Neuron> inputs = detailedNetwork.getInputs(neuron);
                while (inputs.hasNext()) {
                    inputs.next().setShared(true);
                }
            }
        }
        return sharedCount;
    }

    private void setupParentStateNumbers(DetailedNetwork<State.Neural.Structure> network) {
        Map<BaseNeuron, LinkedMapping> neuronOutputs = network.outputMapping;
        neuronOutputs.forEach((neuron, outputs) -> {
            if (neuron.getRawState() instanceof State.Neural.Computation.HasParents || neuron.getRawState() instanceof States.ComputationStateComposite) {
                State.Neural.Computation.HasParents state = (State.Neural.Computation.HasParents) neuron.getComputationView(0);  //all computation views should be exactly the same at this stage
                int parents = state.getParents(null);
                if (parents != 0 && parents != outputs.getLastList().size()) { //if the parents for this neuron are already set differently, we need to store the parentCount in the network
                    State.Neural finalState = (State.Neural.Computation) state;
                    if (settings.parallelTraining) {
                        finalState = State.createCompositeState(finalState, settings.minibatchSize);
                        neuron.setState(finalState);
                    }
                    States.NetworkParents networkParents = new States.NetworkParents(finalState, outputs.getLastList().size());
                    network.addState(neuron, networkParents);
                } else {
                    state.setParents(null, outputs.getLastList().size());
                }
            }
        });
    }

    public Map<BaseNeuron, LinkedMapping> calculateOutputs(TopologicNetwork<State.Neural.Structure> network) {
        Map<BaseNeuron, LinkedMapping> outputMapping = new HashMap<>();

        for (BaseNeuron parent : network.allNeuronsTopologic) {
            Iterator<BaseNeuron> inputs = network.getInputs(parent);
            BaseNeuron child;
            while ((child = inputs.next()) != null) {
                LinkedMapping parentMapping = outputMapping.computeIfAbsent(child, f -> new NeuronMapping());
                parentMapping.addLink(child);
            }
        }
        return outputMapping;
    }

    private StateVisiting.Computation getStatesInitializer(Settings settings) {
        return new ParentsTransfer(-1); //todo more
    }

    /**
     * Choose todo this is only necessary if there are shared neurons or states to store
     *
     * @param neuralNetwork
     * @param states
     */
    public void setupNeuronStatesCache(NeuralNetwork neuralNetwork, State.Structure[] states, StateVisiting.Computation initializer) {
        if (neuralNetwork.getNeuronCount() < settings.lin2bst)
            neuralNetwork.neuronStates = new StatesCache.LinearCache(states, initializer);
        else if (neuralNetwork.getNeuronCount() > settings.lin2bst && neuralNetwork.getNeuronCount() < settings.bst2hashmap)
            neuralNetwork.neuronStates = new StatesCache.HeapCache(states, initializer);
        else
            neuralNetwork.neuronStates = new StatesCache.HashCache(states, initializer);
    }


    /**
     * Create an interface array (!) to be later filled with particular States
     *
     * @param network
     * @param settings
     * @return
     */
    public static State.Structure[] getStates(DetailedNetwork network, Settings settings) {
        State.Structure[] structures = new State.Structure[network.allNeuronsTopologic.size()];
        //todo next fill with particual base states;
        return structures;
    }

    /**
     * Get true state for this neuron of this network
     *
     * @return
     */
    State.Structure getNeuronState(DetailedNetwork network, BaseNeuron neuron) {
        if (network.extraInputMapping // todo next;

    }


    public NeuronMaps getNeuronMaps() {
        return neuralBuilder.neuronFactory.neuronMaps;
    }

    public void setNeuronMaps(NeuronMaps neuronMaps) {
        this.neuralBuilder.neuronFactory.neuronMaps = neuronMaps;
    }
}
