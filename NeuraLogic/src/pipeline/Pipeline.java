package pipeline;

import ida.utils.tuples.Pair;
import settings.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Execution pipeline DAG with nodes as Tasks and edges as pipes. It is your responsibility to create and connect the pipes
 * correctly, and register the terminal (non-streaming) pipes in topological order for scheduling.
 * <p>
 * This is a custom implementation with some hacks but also customization,
 * possible, more generic, 3rd party library : https://dexecutor.github.io/
 * <p>
 * Created by gusta on 14.3.17.
 */
public class Pipeline<S, T> implements Consumer<List<S>>, Supplier<List<T>> {

    private static final Logger LOG = Logger.getLogger(Pipeline.class.getName());

    public String ID;

    protected Settings settings;
    protected List<Pipe<S, S>> starts;
    protected List<Pipe<?, T>> terminals;

    ConcurrentHashMap<String, Branch> branches;
    ConcurrentHashMap<String, Merge> merges;
    ConcurrentHashMap<String, Pipe> pipes;

    ConcurrentHashMap<String, Pipeline> pipelines;

    public Supplier<List<S>> input;
    public Consumer<List<T>> output;

    public Pipeline(String id) {
        this.ID = id;
    }

    /**
     * List of points in the pipeline that need to be called externally, i.e. where streams are terminated.
     */
    //ConcurrentLinkedQueue<Executable> executionQueue;

    public List<Pair<String, T>> execute(S source) {
        //start the whole pipeline
        starts.parallelStream().forEach(start -> start.accept(source));
        /*while (!executionQueue.isEmpty()) {
            Executable poll = executionQueue.poll();
            poll.run();
        }*/

        //collect and return all the terminal results
        return terminals.stream().map(term -> new Pair<>(term.ID, term.get())).collect(Collectors.toList());
    }

    public <I, O> Pipe<I, O> register(Pipe<I, O> p) {
        pipes.put(p.ID, p);
        return p;
    }

    protected <I, O1, O2> Branch<I, O1, O2> register(Branch<I, O1, O2> b) {
        branches.put(b.ID, b);
        return b;
    }

    protected <I1, I2, O> Merge<I1, I2, O> register(Merge<I1, I2, O> m) {
        merges.put(m.ID, m);
        //executionQueue.add(p);
        return m;
    }

    protected <I, O> Pipeline<I, O> register(Pipeline<I, O> p) {
        pipelines.put(p.ID, p);
        return p;
    }

    public <U> Pipeline<S, U> connectAfter(Pipeline<T, U> next) throws IOException {
        if (this.terminals.size() != next.starts.size()) {
            throw new IOException("Pipeline dimensions not matching!");
        }
        for (int i = 0; i < terminals.size(); i++) {
            terminals.get(i).output = next.starts.get(i);
        }
        Pipeline<S, U> pipeline = new Pipeline(this.ID + "+" + next.ID);
        pipeline.starts.addAll(this.starts);
        pipeline.terminals.addAll(next.terminals);
        pipeline.settings = this.settings; //TODO
        pipeline.input = this.input;
        pipeline.output = next.output;

        pipeline.pipelines.put(this.ID, this);
        pipeline.pipelines.put(next.ID, next);
        return pipeline;
    }

    @Override
    public void accept(List<S> sources) {
        if (this.starts.size() != sources.size()) {
            LOG.severe("Pipeline dimensions not matching!");
        }
        for (int i = 0; i < starts.size(); i++) {
            starts.get(i).accept(sources.get(i));
        }
        //TODO no need to wait here?
        List<T> terminalResults = new ArrayList<>();
        for (Pipe<?, T> terminal : terminals) {
            terminalResults.add(terminal.get());
        }
        if (this.output != null) {
            this.output.accept(terminalResults);
        }
    }

    @Override
    public List<T> get() {
        List<T> terminalResults = new ArrayList<>();
        for (Pipe<?, T> terminal : terminals) {
            terminalResults.add(terminal.get());
        }
        return terminalResults;
    }
}