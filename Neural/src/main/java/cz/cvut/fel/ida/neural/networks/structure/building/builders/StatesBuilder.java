package cz.cvut.fel.ida.neural.networks.structure.building.builders;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.StateInitializer;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Evaluator;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters.DropoutRateStrategy;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.States;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.StatesCache;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.NeuronMapping;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import cz.cvut.fel.ida.setup.Settings;

import java.util.*;
import java.util.logging.Logger;

public class StatesBuilder {
    private static final Logger LOG = Logger.getLogger(StatesBuilder.class.getName());

    private Settings settings;

    public StatesBuilder(Settings settings) {
        this.settings = settings;
    }

    /**
     * Infer correct dimensions of all the intermediate Values within this network's neurons' States and their fcnStates
     *
     * @param detailedNetwork
     */
    public void initializeStates(DetailedNetwork<State.Structure> detailedNetwork) {

        int stateIndex = 0;

        StateInitializer stateInitializer = new StateInitializer(detailedNetwork, new Evaluator(stateIndex));   // the Evaluator is just for carrying the index, which is 0 here (no parallel states yet)

        for (int i = 0; i < detailedNetwork.allNeuronsTopologic.size(); i++) {
            BaseNeuron<Neurons, State.Neural> neuron = detailedNetwork.allNeuronsTopologic.get(i);
            if (neuron.getComputationView(stateIndex).getValue() != null)
                continue;   // already initialized, e.g. FactNeurons (or from possible neuron reuse)

            try {

                neuron.visit(stateInitializer);  // not the other way around for correct generic casting...

            } catch (ArithmeticException ex) {
                LOG.severe("Arithmetic exception at neuron: " + neuron.toString());
                throw ex;
            } catch (Exception ex) {
                LOG.severe("Exception at neuron state building (StatesBuilder): " + ex.toString());
                throw ex;
            }
        }
    }


    /**
     * Sets individual dropout rates
     *
     * @param detailedNetwork
     */
    public void setupDropoutStates(DetailedNetwork<State.Neural.Structure> detailedNetwork) {
        DropoutRateStrategy dropoutRateStrategy = new DropoutRateStrategy(settings);
        for (int i = detailedNetwork.allNeuronsTopologic.size() - 1; i > 0; i--) {
            BaseNeuron<Neurons, State.Neural> neuron = detailedNetwork.allNeuronsTopologic.get(i);
            if (neuron.layer == 0) {
                neuron.layer = 1;
            }
            dropoutRateStrategy.setDropout(neuron);
        }
    }

    /**
     * Computes depth (layer index) of each neuron
     *
     * @param detailedNetwork
     */
    public void setupNeuronLayerIndices(DetailedNetwork<State.Neural.Structure> detailedNetwork) {
        for (int i = detailedNetwork.allNeuronsTopologic.size() - 1; i > 0; i--) {
            BaseNeuron<Neurons, State.Neural> neuron = detailedNetwork.allNeuronsTopologic.get(i);
            if (neuron.layer == 0) {
                neuron.layer = 1;
            }
            Iterator<Neurons> inputs = detailedNetwork.getInputs(neuron);
            while (inputs.hasNext()) {
                Neurons next = inputs.next();
                if (next.getLayer() < neuron.layer + 1) //todo check
                    next.setLayer(neuron.layer + 1);    //layer is the max of all paths
            }
        }
    }

    public boolean makeParallel(BaseNeuron neuron) {
        State.Neural.Computation state = neuron.getComputationView(0);
        if (settings.parallelTraining && !(neuron.getRawState() instanceof States.ComputationStateComposite)) {  //if not yet made ready for parallel access
            States.ComputationStateComposite<State.Neural.Computation> compositeState = State.createCompositeState(state, settings.minibatchSize);//todo remove State S from the signature of Neuron? probably yes
            neuron.setState(compositeState);
            return true;
        } else
            return false;
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
    public int makeSharedStatesRecursively(DetailedNetwork<State.Neural.Structure> detailedNetwork) {
        int sharedCount = 0;
        for (int i = detailedNetwork.allNeuronsTopologic.size() - 1; i > 0; i--) {
            BaseNeuron<Neurons, State.Neural> neuron = detailedNetwork.allNeuronsTopologic.get(i);
            if (neuron.isShared) {
                sharedCount++;
                makeParallel(neuron);
                Iterator<Neurons> inputs = detailedNetwork.getInputs(neuron);
                while (inputs.hasNext()) {
                    inputs.next().setShared(true);
                }
            }
        }
        return sharedCount;
    }

    public void addLinkedInputsToNetworkStates(DetailedNetwork<State.Structure> neuralNetwork) {
        neuralNetwork.extraInputMapping.forEach((neuron, inputs) -> {
            if (inputs instanceof NeuronMapping) {
                States.Inputs inputsState = new States.Inputs((NeuronMapping) inputs);
                neuralNetwork.addState(neuron, inputsState);
            } else if (inputs instanceof WeightedNeuronMapping) {
                States.WeightedInputs weightedInputsState = new States.WeightedInputs((WeightedNeuronMapping) inputs);
                neuralNetwork.addState(neuron, weightedInputsState);
            }
        });
    }

    /**
     * Setup parentsCount number into {@link StateVisiting.Computation} states, or
     * extract parentsCount-based States of shared neurons into the networks' {@link State.Structure} states.
     * This will only alter states of neurons which have different number of parents in different networks.
     *
     * @param network
     */
    public void setupParentStateNumbers(DetailedNetwork<State.Neural.Structure> network) {
        Map<BaseNeuron, NeuronMapping<Neurons>> neuronOutputs = network.outputMapping;
        neuronOutputs.forEach((neuron, outputs) -> {
            State.Neural.Computation state = neuron.getComputationView(0); //all computation views should be exactly the same at this stage
            if (state instanceof State.Neural.Computation.HasParents) {
                State.Neural.Computation.HasParents parentsState = (State.Neural.Computation.HasParents) state;
                int parents = parentsState.getParents(null);
                if (parents != 0 && parents != outputs.getLastList().size()) { //if the parents for this neuron are already set differently, we need to store the parentCount in the network
                    neuron.setShared(true); //this automatically implies it is shared
                    neuron.sharedAfterCreation = true;  //we will need to come back to this neuron at the end, after processing all the networks, since the neuron is not shared from the viewpoint of the previous networks
                    State.Neural rawState;
                    //unfortunately we need to take care of the paralelization also here so as to get the right pointer to the final state of the neuron
                    if (settings.parallelTraining) {
                        boolean changed = makeParallel(neuron);
                    }
                    rawState = neuron.getRawState();
                    States.NetworkParents networkParents = new States.NetworkParents(rawState, outputs.getLastList().size());
                    network.addState(neuron, networkParents);
                } else if (parents == 0) { //this is the first time to setup parents for this neuron (it is not shared, at least yet)
                    parentsState.setParents(null, outputs.getLastList().size());
                }
            }
        });
    }


    /**
     * Consider all combinations of cumulatively stored states to produce the final single composite state for fast access
     */
    private State.Structure createFinalState(List<State.Structure> structures) {

        if (structures.size() == 1) {
            return structures.get(0);
        } else if (structures.isEmpty()) {
            return null;
        }

        State.Structure result = null;

        boolean parents = false;
        boolean inputs = false;
        boolean weightedInputs = false;
        boolean outputs = false;

        State.Structure.Parents hasParents = null;
        State.Structure.InputNeuronMap inputNeuronMap = null;
        State.Structure.WeightedInputsMap weightedInputsMap = null;

        for (State.Structure structure : structures) {
            if (structure instanceof State.Structure.Parents) {
                parents = true;
                hasParents = (State.Structure.Parents) structure;
            } else if (structure instanceof State.Structure.InputNeuronMap) {
                inputs = true;
                inputNeuronMap = (State.Structure.InputNeuronMap) structure;
            } else if (structure instanceof State.Structure.WeightedInputsMap) {
                weightedInputs = true;
                weightedInputsMap = (State.Structure.WeightedInputsMap) structure;
            } else if (structure instanceof State.Structure.OutputNeuronMap) {
                outputs = true;
            }
        }

        if (parents && inputs && !weightedInputs && !outputs) {
            result = new States.NetworkParents(hasParents.getParentCounter(), hasParents.getParentCount()).new InputsParents(inputNeuronMap.getInputMapping());
        } else if (parents && !inputs && weightedInputs && !outputs) {
            result = new States.NetworkParents(hasParents.getParentCounter(), hasParents.getParentCount()).new WeightedInputsParents(weightedInputsMap.getWeightedMapping());
        }
        return result;
    }

    /**
     * If we need to either store extra inputs, or store (possibly varying) parents for shared neurons etc., create a Neural Cache.
     * This method turns the accumulated states into their final, optimized, representation to be stored in the cache for fast access.
     * This is only necessary if there are shared neurons/states to store.
     *
     * @param neuralNetwork
     */
    public DetailedNetwork<State.Neural.Structure> setupFinalStatesCache(DetailedNetwork<State.Neural.Structure> neuralNetwork) {

        Map<Neurons, List<State.Structure>> cumulativeStates = neuralNetwork.cumulativeStates;
        if (cumulativeStates.isEmpty()) {
            return neuralNetwork; //no network cache if there is nothing to store
        }
        State.Structure[] structureStates;

        if (settings.iterationMode == Settings.IterationMode.TOPOLOGIC) {
            structureStates = new State.Structure[neuralNetwork.allNeuronsTopologic.size()];
            for (int i = 0; i < neuralNetwork.allNeuronsTopologic.size(); i++) {
                BaseNeuron<Neurons, State.Neural> neuron = neuralNetwork.allNeuronsTopologic.get(i);
                List<State.Structure> structures = cumulativeStates.get(neuron);
                if (structures != null) {
                    State.Structure finalState = createFinalState(structures);
                    structureStates[i] = finalState;
                }
            }
        } else {
            structureStates = new State.Structure[cumulativeStates.size()];

            SortedMap<Integer, State.Structure> finalStates = new TreeMap<>(); //states are sorted by neurons' indices!!
            cumulativeStates.forEach((neuron, structures) -> {
                State.Structure finalState = createFinalState(structures);
                finalStates.put(neuron.getIndex(), finalState);
            });
            finalStates.forEach((index, finalState) -> {
                structureStates[index] = finalState;
            });
        }
        neuralNetwork.neuronStates = StatesCache.getCache(settings, structureStates);
        return neuralNetwork;
    }
}
