package cz.cvut.fel.ida.neural.networks.structure.building.builders;

import cz.cvut.fel.ida.algebra.functions.*;
import cz.cvut.fel.ida.algebra.functions.specific.Average;
import cz.cvut.fel.ida.algebra.functions.specific.Maximum;
import cz.cvut.fel.ida.algebra.functions.specific.Minimum;
import cz.cvut.fel.ida.algebra.functions.specific.Sum;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters.DropoutRateStrategy;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.AggregationState;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.States;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.StatesCache;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.FactNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.NeuronMapping;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.*;
import java.util.logging.Logger;

public class StatesBuilder {
    private static final Logger LOG = Logger.getLogger(StatesBuilder.class.getName());

    private Settings settings;

    public StatesBuilder(Settings settings) {
        this.settings = settings;
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
     * Infer correct dimensions of all the value tensors within this network and create respective {@link Value} objects.
     * todo now now this must respect the actual functions
     *
     * @param detailedNetwork
     */
    public void inferValues(DetailedNetwork<State.Structure> detailedNetwork) {
        for (int i = 0; i < detailedNetwork.allNeuronsTopologic.size(); i++) {
            BaseNeuron<Neurons, State.Neural> neuron = detailedNetwork.allNeuronsTopologic.get(i);
            if (neuron.getComputationView(0).getValue() != null)
                continue;   // already initialized

            try {
                if (neuron instanceof WeightedNeuron) {
                    inferWeightedDimension(detailedNetwork, neuron);
                } else {
                    inferUnweightedDimension(detailedNetwork, neuron);
                }
            } catch (ArithmeticException ex) {
                LOG.severe("algebraic exception at neuron: " + neuron.toString());
                throw ex;
            }
        }
    }

    private void inferWeightedDimension(DetailedNetwork<State.Structure> detailedNetwork, BaseNeuron<Neurons, State.Neural> neuron) {

        if (((BaseNeuron) neuron) instanceof FactNeuron) {
            //facts neurons have no inputs to infer the generics
            return;
        }

        WeightedNeuron<BaseNeuron, State.Neural> weightedNeuron = (WeightedNeuron) neuron;
        List<Value> inputValues = new ArrayList<>();

        Pair<Iterator<BaseNeuron>, Iterator<Weight>> inputs = detailedNetwork.getInputs(weightedNeuron);
        Iterator<BaseNeuron> neuronIterator = inputs.r;
        Iterator<Weight> weightIterator = inputs.s;
        Value value = null;
        Value weight = null;
        if (neuronIterator.hasNext()) {
            BaseNeuron next = neuronIterator.next();
            State.Neural.Computation computationView = next.getComputationView(0);
            value = computationView.getValue();
            Weight nextWeight = weightIterator.next();
            if (nextWeight == null) {
                LOG.finer("Weight for input missing, deducing unit weight for: " + next.name);
                weight = Value.ONE;
            } else {
                weight = nextWeight.value;
            }
        }

        if (value == null || weight == null) {
            LOG.warning("Value dimension cannot be inferred for " + neuron);
            return;
        }
        Value sum = weight.times(value).clone();    //the result might be just value if unit weight -> clone
        if (sum == null) {
            LOG.severe("Weight-Value dimension mismatch at neuron:" + neuron);
        }
        inputValues.add(sum);
        while (neuronIterator.hasNext()) {
            value = neuronIterator.next().getComputationView(0).getValue();
            weight = weightIterator.next().value;
            if (value == null || weight == null) {
                LOG.severe("Value dimension cannot be inferred!" + neuron);
            } else {
                Value increment = weight.times(value);
                if (increment == null) {
                    LOG.severe("Weight-Value dimension mismatch at neuron:" + neuron);
                } else {
                    if (changesDimensions(neuron)) {
                        inputValues.add(increment);
                    } else {
                        Value plus = sum.plus(increment);
                        if (plus == null) {
                            LOG.severe("Input Values dimension mismatch at neuron:" + neuron);
                        } else {
                            sum.incrementBy(increment);
                        }
                    }
                }
            }
        }

        if (changesDimensions(neuron)) {
            sum = neuron.getAggregation().evaluate(inputValues);
            if (neuron.getAggregation() instanceof CrossSum) {
                AggregationState.CrossSumState crossProducState = (AggregationState.CrossSumState) neuron.getComputationView(0).getAggregationState();
                crossProducState.initMapping(inputValues);
            }
        }
        neuron.getComputationView(0).setupValueDimensions(sum);
    }

    private void inferUnweightedDimension(DetailedNetwork<State.Structure> detailedNetwork, BaseNeuron<Neurons, State.Neural> neuron) {
        Iterator<Neurons> inputs = detailedNetwork.getInputs(neuron);
        List<Value> inputValues = new ArrayList<>();

        Value sum = inputs.next().getComputationView(0).getValue();
        if (sum == null) {
            LOG.severe("Value dimension cannot be inferred!" + neuron);
        } else {
            inputValues.add(sum);
            sum = sum.clone();  //we do not want to change any existing value here -> clone
        }
        while (inputs.hasNext()) {
            Neurons next = inputs.next();
            Value result = next.getComputationView(0).getValue();
            if (result == null) {
                LOG.severe("Value dimension cannot be inferred!" + neuron);
            } else {
                if (changesDimensions(neuron)) {
                    inputValues.add(result);
                } else if (neuron.getAggregation().getClass() == Product.class) {
                    Value increment = sum.times(result);
                    if (increment == null) {
                        LOG.severe("Input Values dimension mismatch at neuron:" + neuron);
                    } else {
                        sum = increment;
                    }
                } else {
                    Value increment = sum.plus(result);
                    if (increment == null) {
                        LOG.severe("Input Values dimension mismatch at neuron:" + neuron);
                    } else {
                        sum.incrementBy(result);
                    }
                }
            }
        }
        if (changesDimensions(neuron)) {
            sum = neuron.getAggregation().evaluate(inputValues);
            if (neuron.getAggregation() instanceof CrossSum) {
                AggregationState.CrossSumState crossProducState = (AggregationState.CrossSumState) neuron.getComputationView(0).getAggregationState();
                crossProducState.initMapping(inputValues);
            }
        }
        neuron.getComputationView(0).setupValueDimensions(sum);
    }

    /**
     * Check whether this activaiton needs to be treated specially
     *
     * @param neuron
     * @return
     */
    private boolean changesDimensions(BaseNeuron<Neurons, State.Neural> neuron) {
        return neuron.getAggregation() instanceof CrossSum || neuron.getAggregation() instanceof Concatenation || neuron.getAggregation() instanceof Softmax;
    }

    /**
     * Sets individual dropout rates and ALSO computes depth of each neuron (since this is the only place where it is necessary).
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
            Iterator<Neurons> inputs = detailedNetwork.getInputs(neuron);
            while (inputs.hasNext()) {
                Neurons next = inputs.next();
                if (next.getLayer() < neuron.layer + 1) //todo check
                    next.setLayer(neuron.layer + 1);    //layer is the max of all paths
            }
        }
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

    public static AggregationState getAggregationState(Aggregation aggregation) {
        if (aggregation instanceof CrossSum) {
            return new AggregationState.CrossSumState(aggregation);
        } else if (aggregation instanceof ElementProduct) {
            return new AggregationState.ElementProductState((Activation) aggregation);
        } else if (aggregation instanceof Product) {
            return new AggregationState.ProductState((Activation) aggregation);
        } else if (aggregation instanceof Concatenation) {
            return new AggregationState.ConcatState((Activation) aggregation);
        } else if (aggregation instanceof Average) {
            return new AggregationState.Pooling.Avg(aggregation);
        } else if (aggregation instanceof Maximum) {
            return new AggregationState.Pooling.Max(aggregation);
        } else if (aggregation instanceof Minimum) {
            return new AggregationState.Pooling.Min(aggregation);
        } else if (aggregation instanceof Sum) {
            return new AggregationState.Pooling.Sum(aggregation);
        } else if (aggregation instanceof Softmax) {
            return new AggregationState.SoftmaxState(aggregation);
        } else if (aggregation instanceof SharpMax) {
            return new AggregationState.Pooling.AtomMax(((SharpMax) aggregation).activation);
        } else if (aggregation instanceof SharpMin) {
            return new AggregationState.Pooling.AtomMin(((SharpMin) aggregation).activation);
        } else if (aggregation instanceof Activation) {
            return new AggregationState.ActivationState((Activation) aggregation);
        } else {
            throw new UnsupportedOperationException("unkown Aggregation function state");
        }
    }
}
