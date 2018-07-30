package pipeline;

import java.util.function.Consumer;

public interface ConnectBefore<T>  extends Consumer<T> {

    ConnectAfter<T> getInput();
    void setInput(ConnectAfter<T> prev);

    default ConnectAfter<T> connectBefore(ConnectAfter<T> previous){
        setInput(previous);
        previous.connectAfter(this);
        return previous;
    }
}
