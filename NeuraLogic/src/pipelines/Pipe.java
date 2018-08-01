package pipelines;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * The input can also be a Stream and this pipe can be just a non-terminating mapping Stream<I> -> Stream<O>,
 * but also a mapping (function) between arbitrary kinds of Objects
 * @param <I>
 * @param <O>
 */
public abstract class Pipe<I, O> implements Function<I, O>, ConnectBefore<I>, ConnectAfter<O> {
    private static final Logger LOG = Logger.getLogger(Pipe.class.getName());

    protected Pipe(String id) {
        ID = id;
    }

    public String ID;
    /**
     * Storage of the (intermediate) result of calculation of this Pipe. It will only be not null once someone has
     * actually run (called accept) this Pipe.
     */
    O outputReady;

    public ConnectAfter<I> input;
    public ConnectBefore<O> output;

    /**
     *
     * @param input - can be a Stream!
     */
    public void accept(I input) {
        outputReady = apply(input);
        if (this.output != null) {
            this.output.accept(outputReady);
        }
    }



    public O get() {
        if (outputReady == null) {
            LOG.severe("The result of pipe " + ID + " is requested but not yet calculated");
            LOG.severe("Pipeline is broken");
            System.exit(3);
        }
        return outputReady;
    }


    /**
     * Do not use much, it won't be executed by the run method
     *
     * @param pipe
     */
    private void prepend(ConnectAfter<I> pipe) {
        this.input = pipe;
    }

    public void append(ConnectBefore<O> pipe) {
        this.output = pipe;
    }

    public void insertBefore(Pipe<?, I> before, Pipe<I, I> insert) {
        before.output = insert;
        insert.input = before;
        this.input = insert;
        insert.output = this;
    }
    public void insertAfter(Pipe<O, ?> after, Pipe<O, O> insert) {
        after.input = insert;
        insert.output = after;
        this.output = insert;
        insert.input = this;
    }



    @Override
    public ConnectBefore<O> getOutput() {
        return output;
    }

    @Override
    public void setOutput(ConnectBefore<O> prev) {
        output = prev;
    }

    @Override
    public ConnectAfter<I> getInput() {
        return input;
    }

    @Override
    public void setInput(ConnectAfter<I> prev) {
        input = prev;
    }

}