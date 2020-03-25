package cz.cvut.fel.ida.pipelines;

@FunctionalInterface
public interface CheckedFunction<I, O> {
    O apply(I t) throws Exception;
}