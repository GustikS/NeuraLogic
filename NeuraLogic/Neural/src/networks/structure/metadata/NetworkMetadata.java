package networks.structure.metadata;

import networks.structure.WeightedNeuron;

import java.util.Map;

public class NetworkMetadata {

    public Map<WeightedNeuron, Integer> parentsCount;

    public Map<WeightedNeuron, Double> cumulatedGradient;
}
