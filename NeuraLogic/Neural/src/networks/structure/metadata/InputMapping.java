package networks.structure.metadata;

import ida.utils.tuples.Pair;
import networks.structure.Neuron;
import networks.structure.Weight;

public interface InputMapping<T extends Neuron> extends Iterable<Pair<T, Weight>> {
    void addInput(Pair<T, Weight> input);
}
