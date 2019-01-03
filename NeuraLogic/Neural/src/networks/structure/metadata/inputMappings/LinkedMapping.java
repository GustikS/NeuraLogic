package networks.structure.metadata.inputMappings;

public interface LinkedMapping<T> extends Iterable<T> {
    /**
     * Are the returned inputs a complete set of all inputMappings?
     * todo - they are always complete?
     * @return
     */
    boolean isComplete();

    void addLink(T input);
}