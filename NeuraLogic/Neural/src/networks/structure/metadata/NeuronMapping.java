package networks.structure.metadata;

public interface NeuronMapping<T> extends Iterable<T> {
    /**
     * Are the returned inputs a complete set of all inputs?
     * @return
     */
    boolean isComplete();
    void addLink(T input);
}