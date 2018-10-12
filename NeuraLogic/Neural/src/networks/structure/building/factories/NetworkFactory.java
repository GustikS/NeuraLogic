package networks.structure.building.factories;

import grounding.GroundTemplate;
import networks.structure.networks.types.DetailedNetwork;
import networks.structure.networks.NeuralNetwork;
import networks.structure.metadata.states.NeuronStates;
import networks.structure.metadata.states.State;
import networks.structure.neurons.Neuron;
import networks.structure.neurons.types.*;
import settings.Settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class NetworkFactory {
    private static final Logger LOG = Logger.getLogger(NetworkFactory.class.getName());
    Settings settings;

    public NetworkFactory(Settings settings) {
        this.settings = settings;
    }



    /**
     * Create an interface array (!) to be later filled with particular States
     * @param network
     * @param settings
     * @return
     */
    public static State.Structure[] getStates(DetailedNetwork network, Settings settings) {
        return new State.Structure[network.allNeuronsTopologic.size()];
    }

    /**
     * Get true state for this neuron of this network
     * @return
     */
    State.Structure getNeuronState(NeuralNetwork network, Neuron neuron) {
        if (settings.)
    }

    public NeuralNetwork buildOptimizedNetwork(){

    }

    public DetailedNetwork buildDetailedNetwork(GroundTemplate.NeuronMaps neuronMaps, Set<NegationNeuron> negationNeurons) {
        DetailedNetwork detailedNetwork = buildDetailedNetwork(neuronMaps.atomNeurons.values(), neuronMaps.aggNeurons.values(), neuronMaps.ruleNeurons.values(), neuronMaps.factNeurons.values(), negationNeurons);

        if (neuronMaps.extraInputMapping != null && !neuronMaps.extraInputMapping.isEmpty()) {
            detailedNetwork.extraInputMapping = new HashMap<>();
            detailedNetwork.extraInputMapping.putAll(neuronMaps.extraInputMapping);
        }
        return detailedNetwork;
    }

    public DetailedNetwork buildDetailedNetwork(Collection<AtomNeuron> atomNeurons, Collection<AggregationNeuron> aggregationNeurons, Collection<RuleNeurons> ruleNeurons, Collection<FactNeuron> factNeurons, Collection<NegationNeuron> negationNeurons) {

        DetailedNetwork network = new DetailedNetwork();

        network.neurons = network.new Neurons();
        network.neurons.atomNeurons = new ArrayList<>(atomNeurons);
        network.neurons.aggNeurons = new ArrayList<>(aggregationNeurons);
        network.neurons.ruleNeurons = new ArrayList<>();
        network.neurons.weightedRuleNeurons = new ArrayList<>();
        for (RuleNeurons ruleNeuron : ruleNeurons) {
            if (ruleNeuron instanceof WeightedRuleNeuron) {
                network.neurons.weightedRuleNeurons.add((WeightedRuleNeuron) ruleNeuron);
            } else {
                network.neurons.ruleNeurons.add((RuleNeuron) ruleNeuron);
            }
        }
        network.neurons.factNeurons = new ArrayList<>(factNeurons);
        network.neurons.negationNeurons = new ArrayList<>(negationNeurons);

        network.allNeuronsTopologic = new ArrayList<>(atomNeurons.size() + aggregationNeurons.size() + ruleNeurons.size() + factNeurons.size() + negationNeurons.size());
        network.allNeuronsTopologic.addAll(atomNeurons);
        network.allNeuronsTopologic.addAll(aggregationNeurons);
        network.allNeuronsTopologic.addAll(network.neurons.ruleNeurons);
        network.allNeuronsTopologic.addAll(network.neurons.weightedRuleNeurons);
        network.allNeuronsTopologic.addAll(factNeurons);
        network.allNeuronsTopologic.addAll(negationNeurons);

        network.allNeuronsTopologic = network.topologicSort(network.allNeuronsTopologic);

        network.outputMapping = network.calculateOutputs();

        setNeuronSearch(network, getStates(network, settings));

        return network;
    }

    /**
     * Choose
     * @param neuralNetwork
     * @param states
     */
    public void setNeuronSearch(NeuralNetwork neuralNetwork, State.Structure[] states) {
        if (neuralNetwork.getSize() < settings.lin2bst)
            neuralNetwork.neuronStates = new NeuronStates.LinearCache(states);
        else if (neuralNetwork.getSize() > settings.lin2bst && neuralNetwork.getSize() < settings.bst2hashmap)
            neuralNetwork.neuronStates = new NeuronStates.HeapCache(states);
        else
            neuralNetwork.neuronStates = new NeuronStates.HashCache(states);
    }

}