package networks.structure.metadata;

import ida.utils.tuples.Pair;
import networks.structure.Neuron;
import networks.structure.Weight;

public interface NeuronMapping<T extends Neuron> extends Iterable<Pair<T, Weight>> {
    void addLink(Pair<T, Weight> input);
}
