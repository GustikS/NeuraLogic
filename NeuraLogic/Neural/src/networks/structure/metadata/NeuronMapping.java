package networks.structure.metadata;

import networks.structure.networks.State;

public interface NeuronMapping<T> extends Iterable<T>, State.Structure {
    /**
     * Are the returned inputs a complete set of all inputs?
     * @return
     */
    boolean isComplete();

    void addLink(T input);
}