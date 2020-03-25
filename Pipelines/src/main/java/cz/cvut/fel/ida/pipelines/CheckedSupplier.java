package cz.cvut.fel.ida.pipelines;

@FunctionalInterface
public interface CheckedSupplier<O> {
    O get() throws Exception;
}