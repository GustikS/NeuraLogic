package networks.structure.metadata.inputMappings;

import networks.structure.metadata.states.State;

public interface NeuronMapping<T> extends Iterable<T>, State.Structure {
    /**
     * Are the returned inputs a complete set of all inputMappings?
     * @return
     */
    boolean isComplete();

    void addLink(T input);
}