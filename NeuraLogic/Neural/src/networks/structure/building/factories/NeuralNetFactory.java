package networks.structure.building.factories;

import networks.structure.building.NeuronMaps;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.NeuronSets;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.types.*;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.components.types.TopologicNetwork;
import settings.Settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class NeuralNetFactory {
    private static final Logger LOG = Logger.getLogger(NeuralNetFactory.class.getName());
    Settings settings;

    public NeuralNetFactory(Settings settings) {
        this.settings = settings;
    }

    /**
     * todo must after creation and post-processing, add a transformation to a more optimized version (everything based on int, maybe even precompute layers, remove recursion)
     * @return
     */
    public NeuralNetwork extractOptimizedNetwork(DetailedNetwork network) {

    }

    public TopologicNetwork extractTopologicNetwork(DetailedNetwork network){
        //todo next this will require building the states cache
    }

    public DetailedNetwork createDetailedNetwork(NeuronMaps neuronMaps, String id) {
        DetailedNetwork detailedNetwork = createDetailedNetwork(id, neuronMaps.atomNeurons.values(), neuronMaps.aggNeurons.values(), neuronMaps.ruleNeurons.values(), neuronMaps.factNeurons.values(), neuronMaps.negationNeurons);

        //we take input overmaps from the neuronMaps, otherwise the inputs of the previously created neurons would be wrong
        if (neuronMaps.extraInputMapping != null && !neuronMaps.extraInputMapping.isEmpty()) {
            detailedNetwork.extraInputMapping = new HashMap<>();
            detailedNetwork.extraInputMapping.putAll(neuronMaps.extraInputMapping);
        }
        return detailedNetwork;
    }

    private DetailedNetwork createDetailedNetwork(String id, Collection<AtomNeuron> atomNeurons, Collection<AggregationNeuron> aggregationNeurons, Collection<RuleNeurons> ruleNeurons, Collection<FactNeuron> factNeurons, Collection<NegationNeuron> negationNeurons) {

        NeuronSets neurons = new NeuronSets(atomNeurons, aggregationNeurons, ruleNeurons, factNeurons, negationNeurons);

        List<BaseNeuron> allNeurons = new ArrayList<>(atomNeurons.size() + aggregationNeurons.size() + ruleNeurons.size() + factNeurons.size() + negationNeurons.size());
        allNeurons.addAll(neurons.atomNeurons);
        allNeurons.addAll(neurons.aggNeurons);
        allNeurons.addAll(neurons.ruleNeurons);
        allNeurons.addAll(neurons.weightedRuleNeurons);
        allNeurons.addAll(neurons.factNeurons);
        allNeurons.addAll(neurons.negationNeurons);

        DetailedNetwork network = new DetailedNetwork(id, allNeurons, neurons);

        return network;
    }
}