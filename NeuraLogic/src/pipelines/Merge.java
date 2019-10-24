package pipelines;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Merge blocks are special as they need external execution (there is no single predecessor that could induce it by itself)
 *
 * @param <I1>
 * @param <I2>
 * @param <O>
 */
public abstract class Merge<I1, I2, O> extends Block implements ConnectAfter<O> {
    private static final Logger LOG = Logger.getLogger(Merge.class.getName());

    public Pipe<I1, I1> input1;
    public Pipe<I2, I2> input2;
    public ConnectBefore<O> output;

    /**
     * Storage of the (intermediate) result of calculation of this Pipe. It will only be not null once someone has
     * actually run (called accept) this Pipe.
     */
    O outputReady;

    public Merge(String id) {
        ID = id;
        input1 = new Pipe<I1, I1>(id + "-Input1") {
            @Override
            public I1 apply(I1 i1) {
                this.outputReady = i1;
                Merge.this.accept(i1);
                return i1;
            }
        };
        input2 = new Pipe<I2, I2>(id + "-Input2") {
            @Override
            public I2 apply(I2 i2) {
                this.outputReady = i2;
                Merge.this.accept(i2);
                return i2;
            }
        };
    }

    @Override
    public O get() {
        if (outputReady == null) {
            LOG.severe("The result of this Merge " + ID + " is requested but not yet calculated (backtracking in the execution graph).");
        }

        return outputReady;
    }

    /**
     * Call by either one of the inputs
     *
     * @param o
     */
    public void accept(Object o) {
        I1 i1;
        I2 i2;
        if ((i1 = input1.get()) != null && (i2 = input2.get()) != null) {
            LOG.finer("Entering: " + ID);
            accept(i1, i2);
        }
        else
            LOG.finer("Backtracking: " + ID );
    }

    public void accept(I1 input1, I2 input2) {
        outputReady = merge(input1, input2);
        if (output != null)
            output.accept(outputReady);
    }


    /*
        private Stream<O> merge(Stream<I1> input1, I2 input2) {
            return input1.map(i1 -> merge(i1, input2));
        }

        private Stream<O> merge(I1 input1, Stream<I2> input2) {
            return input2.map(i2 -> merge(input1, i2));
        }

        private Stream<O> merge(Stream<I1> input1, Stream<I2> input2) {
            return Utilities.zipStreams(input1, input2, this::merge);
        }
    */
    protected abstract O merge(I1 input1, I2 input2);

    @Override
    public ConnectBefore<O> getOutput() {
        return output;
    }

    @Override
    public void setOutput(ConnectBefore<O> prev) {
        output = prev;
    }

    //Note - it will be probably better to explicitly differentiate between I1 and I2
    /*
    private final Class<I1> i1;
    private final Class<I2> i2;

    public <T> ConnectAfter<T> connectBefore(ConnectAfter<T> prev, T cls){
        if (cls.getClass() == i1.getClass())
        return prev;
    }
    */

    public ConnectAfter<I1> connectBeforeL(ConnectAfter<I1> prev) {
        return input1.connectBefore(prev);
    }

    public ConnectAfter<I2> connectBeforeR(ConnectAfter<I2> prev) {
        return input2.connectBefore(prev);
    }



    public List<Merge<I1, I2, O>> parallel(int count) {
        List<Merge<I1, I2, O>> copies = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            copies.add(new Merge<I1, I2, O>(this.ID + i) {
                @Override
                protected O merge(I1 input1, I2 input2) {
                    return Merge.this.merge(input1, input2);
                }
            });
        }
        return copies;
    }

    public static <T, A extends Merge<T, ?, ?>, B extends ConnectAfter<T>> void connectBeforeL(List<A> merges, List<B> pipes){
        if (merges.size() != pipes.size()){
            LOG.severe("The 2 Lists of merges and pipes provided cannot be connected with different sizes!");
        }
        for (int i = 0; i < merges.size(); i++) {
            merges.get(i).connectBeforeL(pipes.get(i));
        }
    }

    public static <T, A extends Merge<?, T, ?>, B extends ConnectAfter<T>> void connectBeforeR(List<A> merges, List<B> pipes){
        if (merges.size() != pipes.size()){
            LOG.severe("The 2 Lists of merges and pipes provided cannot be connected with different sizes!");
        }
        for (int i = 0; i < merges.size(); i++) {
            merges.get(i).connectBeforeR(pipes.get(i));
        }
    }

    public static <T, A extends Merge<?, ?, T>, B extends ConnectBefore<T>> void connectAfter(List<A> merges, List<B> pipes){
        if (merges.size() != pipes.size()){
            LOG.severe("The 2 Lists of merges and pipes provided cannot be connected with different sizes!");
        }
        for (int i = 0; i < merges.size(); i++) {
            merges.get(i).connectAfter(pipes.get(i));
        }
    }
}
