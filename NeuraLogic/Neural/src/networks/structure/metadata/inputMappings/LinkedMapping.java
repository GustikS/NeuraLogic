package networks.structure.metadata.inputMappings;

import networks.structure.components.weights.Weight;

import java.util.Iterator;
import java.util.List;

public interface LinkedMapping<T> extends Iterable<T> {
    /**
     * Are the returned inputs a complete set of all inputMappings?
     * todo - they are always complete?
     * @return
     */
    boolean isComplete();

    List<T> getLastList();

    void addLink(T input);

    interface WeightMapping {

        void addWeight(Weight weight);

        Iterator<Weight> weightIterator();

    }
}