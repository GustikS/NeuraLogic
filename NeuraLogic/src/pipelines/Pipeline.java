package pipelines;

import pipelines.building.AbstractPipelineBuilder;
import settings.Settings;
import utils.Exporter;
import utils.generic.Pair;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Execution pipelines DAG with nodes as Tasks and edges as pipes. It is your responsibility to create and connect the pipes
 * correctly, and register the terminal (non-streaming) pipes in topological order for scheduling.
 * <p>
 * This is a custom implementation with some hacks but also customization,
 * possible, more generic, 3rd party library : https://dexecutor.github.io/
 * <p>
 * Created by gusta on 14.3.17.
 */
public class Pipeline<S, T> extends Block implements ConnectBefore<S>, ConnectAfter<T>, Function<S, T> {
    // pipelines - vstup a vystup by byl Pipe/Merge/Branch?

    private static final Logger LOG = Logger.getLogger(Pipeline.class.getName());

    public AbstractPipelineBuilder<S,T> originalBuilder;

    /**
     * first node INSIDE this pipeline
     */
    public ConnectBefore<S> start;
    /**
     * last node INSIDE this pipeline
     */
    public ConnectAfter<T> terminal;

    ConcurrentHashMap<String, Branch> branches = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Merge> merges = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Pipe> pipes = new ConcurrentHashMap<>();

    @Deprecated
    ConcurrentHashMap<String, ParallelPipe> multiPipes = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, MultiBranch> multiBranches = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, MultiMerge> multiMerges = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, Pipeline> pipelines = new ConcurrentHashMap<>();

    /**
     * input BEFORE this pipeline
     */
    public ConnectAfter<S> input;
    /**
     * output AFTER this pipeline
     */
    public ConnectBefore<T> output;

    /**
     * This pipeline needs to be rebuilt before execution
     */
    private boolean invalidated = false;


    private Pipeline(String id) {
        this.ID = id;
    }

    public Pipeline(String id, AbstractPipelineBuilder<S,T> originalBuilder){
        this.ID = id;
        this.originalBuilder = originalBuilder;
        this.settings = originalBuilder.settings;
    }

    public Pipeline(String id, Settings settings) {
        this.ID = id;
        this.settings = settings;
    }

    public Pipeline(String id, Settings settings, Exporter exporter) {
        this.ID = id;
        this.settings = settings;
        this.exporter = exporter;
    }

    /**
     * List of points in the pipelines that need to be called externally, i.e. where streams are terminated.
     */
    //ConcurrentLinkedQueue<Executable> executionQueue;
    public Pair<String, T> execute(S source) {
        //start the whole pipelines
        start.accept(source);
        /*while (!executionQueue.isEmpty()) {
            Executable poll = executionQueue.poll();
            poll.run();
        }*/

        //collect and return all the terminal results
        return new Pair<String, T>(ID, terminal.get());
    }

    public <I, O, A extends Pipe<I, O>> A register(A p) {
        pipes.put(p.ID, p);
        p.parent = this;
        return p;
    }

    @Deprecated
    public <I, O, A extends ParallelPipe<I, O>> A register(A m) {
        //TODO register individual pipes in multis
        multiPipes.put(m.ID, m);
        return m;
    }

    public <I, O1, O2, A extends Branch<I, O1, O2>> A register(A b) {
        branches.put(b.ID, b);
        b.parent = this;
        return b;
    }

    public <I1, I2, O, A extends Merge<I1, I2, O>> A register(A m) {
        merges.put(m.ID, m);
        m.parent = this;
        return m;
    }

    public <I, O> Pipeline<I, O> register(Pipeline<I, O> p) {
        pipelines.put(p.ID, p);
        p.parent = this;
        return p;
    }

    public <I, O, A extends MultiBranch<I, O>> A register(A m) {
        multiBranches.put(m.ID, m);
        m.parent = this;
        return m;
    }

    public <I, O, A extends MultiMerge<I, O>> A register(A m) {
        multiMerges.put(m.ID, m);
        m.parent = this;
        return m;
    }

    public <O, A extends Pipe<S, O>> A registerStart(A p) {
        start = p;
        register(p);
        return p;
    }

    public <O1, O2, A extends Branch<S, O1, O2>> A registerStart(A p) {
        start = p;
        register(p);
        return p;
    }

    public <O, A extends MultiBranch<S, O>> A registerStart(A m) {
        start = m;
        register(m);
        return m;
    }

    public <O, A extends Pipeline<S, O>> A registerStart(A p) {
        start = p;
        register(p);
        return p;
    }

    public <I, A extends Pipe<I, T>> A registerEnd(A p) {
        terminal = p;
        register(p);
        return p;
    }


    public <I1, I2, A extends Merge<I1, I2, T>> A registerEnd(A p) {
        terminal = p;
        register(p);
        return p;
    }

    public <I, A extends MultiMerge<I, T>> A registerEnd(A p) {
        terminal = p;
        register(p);
        return p;
    }

    public <I, A extends Pipeline<I, T>> A registerEnd(A p) {
        terminal = p.terminal;
        register(p);
        return p;
    }

    public <U> Pipeline<S, U> mergeAfter(Pipeline<T, U> next) {
        Pipeline<S, U> pipeline = new Pipeline(this.ID + "+" + next.ID);
        pipeline.start = this.start;
        pipeline.terminal = next.terminal;
        pipeline.settings = this.settings; //TODO
        pipeline.input = this.input;
        pipeline.output = next.output;

        pipeline.pipelines.put(this.ID, this);
        pipeline.pipelines.put(next.ID, next);
        return pipeline;
    }

    @Override
    public void accept(S sources) {
        LOG.finer("Entering pipeline: " + ID);
        if (this.invalidated){
            this.rebuild(settings);
        }
        start.accept(sources);
        //all the processing happen recursively here
        if (this.output != null) {
            this.output.accept(terminal.get());
        }
    }

    public void rebuild(Settings settings) {
        LOG.info("Rebuilding pipeline " + this.ID);
        Pipeline<S,T> pipeline = originalBuilder.buildPipeline();
        if (!this.start.toString().equals(pipeline.start.toString())){
            LOG.warning("Pipeline start changed after rebuild from " + this.start.toString() + " to " + pipeline.start.toString());
        }
        if (!this.terminal.toString().equals(pipeline.terminal.toString())){
            LOG.warning("Pipeline terminal changed after rebuild from " + this.terminal.toString() + " to " + pipeline.terminal.toString());
        }
        pipeline.start.setInput(this.start.getInput());
        pipeline.terminal.setOutput(this.terminal.getOutput());
        this.copyFrom(pipeline);
    }

    private void copyFrom(Pipeline<S, T> pipeline) {
        this.start = pipeline.start;
        this.terminal = pipeline.terminal;
        this.branches = pipeline.branches;
        this.merges = pipeline.merges;
        this.pipes = pipeline.pipes;
        this.multiBranches = pipeline.multiBranches;
        this.multiMerges = pipeline.multiMerges;
    }

    @Override
    public T get() {
        return terminal.get();
    }

    @Override
    public ConnectBefore<T> getOutput() {
        return output;
    }

    @Override
    public void setOutput(ConnectBefore<T> prev) {
        output = prev;
    }

    @Override
    public ConnectAfter<S> getInput() {
        return input;
    }

    @Override
    public void setInput(ConnectAfter<S> prev) {
        input = prev;
    }

    @Override
    public T apply(S s) {
        return execute(s).s;
    }

    public Pipeline findPipeline(String id){
        if (this.ID.equals(id)){
            return this;
        } else {
            Pipeline pipeline = pipelines.get(id);
            if (pipeline != null){
                return pipeline;
            } else {
                for (Pipeline value : pipelines.values()) {
                    Pipeline inner = value.findPipeline(id);
                    if (inner != null) {
                        return inner;
                    }
                }
            }
        }
        return null;
    }

}