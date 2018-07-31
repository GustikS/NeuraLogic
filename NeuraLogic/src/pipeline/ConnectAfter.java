package pipeline;

import java.util.function.Supplier;

public interface ConnectAfter<T> extends Supplier<T> {

    ConnectBefore<T> getOutput();
    void setOutput(ConnectBefore<T> prev);

    default ConnectBefore<T> connectAfter(ConnectBefore<T> next) {
        setOutput(next);
        next.setInput(this);
        return next;
    }
}