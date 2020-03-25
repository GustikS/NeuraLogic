package cz.cvut.fel.ida.pipelines;

@FunctionalInterface
public interface CheckedConsumer<I> {
    void accept(I t) throws Exception;
}