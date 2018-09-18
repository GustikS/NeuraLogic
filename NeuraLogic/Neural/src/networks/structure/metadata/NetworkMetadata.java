package networks.structure.metadata;

import networks.structure.Neuron;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class NetworkMetadata {

    public @Nullable Map<Neuron, InputMapping> extraOutputMapping;
}
