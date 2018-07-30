package pipeline;

import java.util.function.Supplier;

public interface ConnectAfter<T> extends Supplier<T> {

    ConnectBefore<T> getOutput();
    void setOutput(ConnectBefore<T> prev);

    default <A extends ConnectBefore<T>> A connectAfter(A next) {
        setOutput(next);
        next.connectBefore(this);
        return next;
    }
}