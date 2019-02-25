package networks.structure.metadata;

import networks.structure.components.neurons.WeightedNeuron;

import java.util.Map;

public class NetworkMetadata {

    @Deprecated
    public Map<WeightedNeuron, Integer> parentsCount;


    @Deprecated
    public Map<WeightedNeuron, Double> cumulatedGradient;
}
