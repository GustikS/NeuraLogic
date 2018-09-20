package networks.structure.metadata;

public interface NeuronMapping<T> extends Iterable<T> {
    void addLink(T input);
}