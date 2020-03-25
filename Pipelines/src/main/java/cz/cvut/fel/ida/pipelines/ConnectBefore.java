package cz.cvut.fel.ida.pipelines;

public interface ConnectBefore<T>  extends CheckedConsumer<T> {

    ConnectAfter<T> getInput();
    void setInput(ConnectAfter<T> prev);

    default ConnectAfter<T> connectBefore(ConnectAfter<T> previous){
        setInput(previous);
        previous.setOutput(this);
        return previous;
    }

    default <X> Pipe<X, T> connectBefore(Pipe<X,T> previous) {
        setInput(previous);
        previous.setOutput(this);
        return previous;
    }
}
