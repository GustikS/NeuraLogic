package cz.cvut.fel.ida.pipelines;

import cz.cvut.fel.ida.setup.Settings;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * The input can also be a Stream and this pipe can be just a non-terminating mapping Stream<I> -> Stream<O>,
 * but also a mapping (function) between arbitrary kinds of Objects
 *
 * @param <I>
 * @param <O>
 */
public abstract class Pipe<I, O> extends Block implements Function<I, O>, ConnectBefore<I>, ConnectAfter<O> {
    private static final Logger LOG = Logger.getLogger(Pipe.class.getName());


    protected Pipe(String id) {
        this(id, null);
    }

    protected Pipe(String id, Settings settings) {
        this.settings = settings;
        this.ID = id;
        this.exporter = createExporter(id, settings);
    }

    /**
     * Storage of the (intermediate) result of calculation of this Pipe. It will only be not null once someone has
     * actually run (called accept) this Pipe.
     */
    O outputReady;

    public ConnectAfter<I> input;
    public ConnectBefore<O> output;

    /**
     * @param input - can be a Stream!
     */
    public void accept(I input) {
        LOG.finer("Entering: " + ID);
        outputReady = apply(input);

        this.export(outputReady);

        if (this.output != null) {
            this.output.accept(outputReady);
        }
    }

    public O get() {
        if (outputReady == null) {
            LOG.finer("Backtracking pipe " + ID);
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

    /**
     * Reflection based cloning - hack to clone abstract pipe subclasses
     * Rather use parallel instead of reflection!
     *
     * @param count
     * @return
     */
    @Deprecated
    public List<Pipe<I, O>> multiple(int count) {
        List<Pipe<I, O>> copies = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            try {
                Pipe<I, O> clone = this.getClass().getDeclaredConstructor(String.class).newInstance(this.ID + i);
                copies.add(clone);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if (!copies.isEmpty()) return copies;
        else {
            LOG.severe("Cloning of a pipe:" + this.ID + " was not succesfull!");
            return null;
        }
    }

    public List<Pipe<I, O>> parallel(int count) {
        List<Pipe<I, O>> copies = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Pipe<I, O> clone = new Pipe<I, O>(this.ID + i) {
                @Override
                public O apply(I i) {
                    return Pipe.this.apply(i);  //TODO check if working correctly
                }
            };
        }
        return copies;
    }

    public static <T, A extends ConnectAfter<T>, B extends ConnectBefore<T>> void connect(List<A> prev, List<B> next) {
        if (prev.size() != next.size()) {
            LOG.severe("The 2 Lists of pipes provided cannot be connected with different sizes!");
        }
        for (int i = 0; i < prev.size(); i++) {
            prev.get(i).connectAfter(next.get(i));
        }
    }

}