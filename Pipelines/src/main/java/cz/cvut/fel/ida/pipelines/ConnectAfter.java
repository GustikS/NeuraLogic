package cz.cvut.fel.ida.pipelines;

public interface ConnectAfter<T> extends CheckedSupplier<T> {

    ConnectBefore<T> getOutput();

    void setOutput(ConnectBefore<T> prev);

    default ConnectBefore<T> connectAfter(ConnectBefore<T> next) {
        setOutput(next);
        next.setInput(this);
        return next;
    }

    default <X> Pipe<T, X> connectAfter(Pipe<T, X> next) {
        setOutput(next);
        next.setInput(this);
        return next;
    }

    default <X> Pipeline<T, X> connectAfter(Pipeline<T, X> next) {
        setOutput(next);
        next.setInput(this);
        return next;
    }
}