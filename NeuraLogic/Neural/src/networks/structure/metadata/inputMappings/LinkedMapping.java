package networks.structure.metadata.inputMappings;

import networks.structure.components.weights.Weight;

import java.util.Iterator;

public interface LinkedMapping<T> extends Iterable<T> {
    /**
     * Are the returned inputs a complete set of all inputMappings?
     * todo - they are always complete?
     * @return
     */
    boolean isComplete();

    void addLink(T input);

    public interface WeightMapping {

        Iterator<Weight> weightIterator();

    }
}