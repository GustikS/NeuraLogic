package cz.cvut.fel.ida.pipelines;

import cz.cvut.fel.ida.setup.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * This block may be non-terminating - it is possible to return merging of streams as a stream,
 * hence the return value is open - it can be a Stream, List, or any Object
 */
public abstract class MultiMerge<I, O> extends Block implements ConnectAfter<O>{
    private static final Logger LOG = Logger.getLogger(MultiMerge.class.getName());

    public List<Pipe<I,I>> inputs;
    public ConnectBefore<O> output;

    private ConcurrentLinkedQueue<I> inputsReady;

    public O outputReady;

    protected MultiMerge(String id, int count, Settings settings) {
        this.settings = settings;
        this.ID = id;

        inputs = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            inputs.add(new Pipe<I, I>(id + "Input" + i) {
                @Override
                public I apply(I i) throws Exception {
                    MultiMerge.this.accept(i);
                    return i;
                }
            });
        }
        inputsReady = new ConcurrentLinkedQueue<>();
    }

    private void accept(I i) throws Exception {
        inputsReady.add(i);
        if (inputsReady.size() == inputs.size()){
            LOG.finer("Entering: " + ID);
            accept(new ArrayList<>(inputsReady));
        }
    }

    @Override
    public O get() {
        if (outputReady == null) {
            LOG.severe("The result of this MultiMerge " + ID + " is requested but not yet calculated");
            LOG.severe("Pipeline is broken");
            System.exit(3);
        }
        return outputReady;
    }

    /**
     * Self computation (possibly parallel) and invocation of successor Consumer of O
     *
     * @param allInputs
     */
    public void accept(List<I> allInputs) throws Exception {
        outputReady = merge(allInputs);
        if (this.output != null) {
            this.output.accept(outputReady);
        }
    }

    protected abstract O merge(List<I> inputs);

    @Override
    public ConnectBefore<O> getOutput() {
        return output;
    }

    @Override
    public void setOutput(ConnectBefore<O> prev) {
        output = prev;
    }

    public <T extends ConnectAfter<I>> List<T> connectBefore(List<T> prev){
        if (prev.size() != inputs.size()){
            LOG.severe("MultiMerge input dimension mismatches with preceding providers!");
        }
        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).connectBefore(prev.get(i));
        }
        return prev;
    }
}