package pipeline;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * The input can also be a Stream and this pipe can be just a non-terminating mapping Stream<I> -> Stream<O>,
 * but also a mapping (function) between arbitrary kinds of Objects
 * @param <I>
 * @param <O>
 */
public abstract class Pipe<I, O> implements Function<I, O>, Consumer<I>, Supplier<O> {
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

    public Supplier<I> input;
    public Consumer<O> output;

    /**
     * Do not use much, it won't be executed by the run method
     *
     * @param pipe
     */
    private void prepend(Supplier<I> pipe) {
        this.input = pipe;
    }

    public void append(Consumer<O> pipe) {
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

    public void connectAfter(Pipe<O,?> after){
        this.output = after;
        after.input = this;
    }

    public void connectBefore(Pipe<?,I> before){
        this.input = before;
        before.output = this;
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
     *
     * @param input - can be a Stream!
     */
    public void accept(I input) {
        outputReady = transform(input);
        if (this.output != null) {
            this.output.accept(outputReady);
        }
    }

    O transform(I input) {
        return apply(input);
    }
}