package networks.structure.metadata;

import networks.structure.Neuron;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class NetworkMetadata {

    public Map<Neuron, Integer> parentsCount;

    public Map<Neuron, Double> cumulatedGradient;
}
