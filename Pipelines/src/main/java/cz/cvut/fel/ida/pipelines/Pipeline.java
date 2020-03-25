package cz.cvut.fel.ida.pipelines;

import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exporter;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.Timing;

import java.util.concurrent.ConcurrentHashMap;
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
public class Pipeline<S, T> extends Block implements ConnectBefore<S>, ConnectAfter<T>, CheckedFunction<S, T> {
    // pipelines - vstup a vystup by byl Pipe/Merge/Branch?

    private static final Logger LOG = Logger.getLogger(Pipeline.class.getName());
    public Timing timing;

    transient public AbstractPipelineBuilder<S, T> originalBuilder;

    /**
     * first node INSIDE this pipeline
     */
    public ConnectBefore<S> start;
    /**
     * last node INSIDE this pipeline
     */
    public ConnectAfter<T> terminal;

    public ConcurrentHashMap<String, Branch> branches = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Merge> merges = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Pipe> pipes = new ConcurrentHashMap<>();

    @Deprecated
    public
    ConcurrentHashMap<String, ParallelPipe> multiPipes = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, MultiBranch> multiBranches = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, MultiMerge> multiMerges = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, Pipeline> pipelines = new ConcurrentHashMap<>();

    /**
     * input BEFORE this pipeline
     */
    transient public ConnectAfter<S> input;
    /**
     * output AFTER this pipeline
     */
    transient public ConnectBefore<T> output;

    /**
     * This pipeline needs to be rebuilt before execution
     */
    private boolean invalidated = false;


    private Pipeline(String id) {
        this.ID = id;
    }

    public Pipeline(String id, AbstractPipelineBuilder<S, T> originalBuilder) {
        this(id, originalBuilder.settings);
        this.originalBuilder = originalBuilder;
    }

    public Pipeline(String id, Settings settings) {
        this(id, settings, createExporter(id, settings));
    }

    public Pipeline(String id, Settings settings, Exporter exporter) {
        this.ID = id;
        this.settings = settings;
        this.exporter = exporter;
        this.timing = new Timing();
    }

    /**
     *
     */
    //ConcurrentLinkedQueue<Executable> executionQueue;
    public Pair<String, T> execute(S source) throws Exception {
        LOG.info("Executing pipeline : " + this.ID);
        this.accept(source);
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
        register(b.output1);
        register(b.output2);
        b.parent = this;
        return b;
    }

    public <I1, I2, O, A extends Merge<I1, I2, O>> A register(A m) {
        merges.put(m.ID, m);
        register(m.input1);
        register(m.input2);
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
    public void accept(S sources) throws Exception {
        LOG.finer("Entering pipeline: " + ID);
        if (this.invalidated) {
            this.rebuild(settings);
        }
        timing.tic();
        //start the whole pipelines
        start.accept(sources);

        timing.toc();
        timing.finish();
        super.export(terminal.get(), timing);

        if (this.output != null) {
            this.output.accept(terminal.get());
        }
    }

    public void rebuild(Settings settings) {
        LOG.info("Rebuilding pipeline " + this.ID);
        Pipeline<S, T> pipeline = originalBuilder.buildPipeline();
        if (!this.start.toString().equals(pipeline.start.toString())) {
            LOG.warning("Pipeline start changed after rebuild from " + this.start.toString() + " to " + pipeline.start.toString());
            if (this.parent.start == this.start) {
                this.parent.start = pipeline.start;
            }
        }
        if (!this.terminal.toString().equals(pipeline.terminal.toString())) {
            LOG.warning("Pipeline terminal changed after rebuild from " + this.terminal.toString() + " to " + pipeline.terminal.toString());
            if (this.parent.terminal == this.terminal) {
                this.parent.terminal = pipeline.terminal;
            }
        }
        pipeline.start.setInput(this.start.getInput());
        pipeline.terminal.setOutput(this.terminal.getOutput());
        this.copyFrom(pipeline);
    }

    public boolean rebuildPipeline(String name) {
        Pipeline groundingPipeline = getRoot().findPipeline(name);
        if (groundingPipeline == null) {
            LOG.severe("Not able to rebuild " + name + " pipeline !!");
            return false;
        } else {
            groundingPipeline.rebuild(settings);
            return true;
        }
    }

    private void copyFrom(Pipeline<S, T> pipeline) {
        this.start = pipeline.start;
        this.terminal = pipeline.terminal;
        this.branches = pipeline.branches;
        this.merges = pipeline.merges;
        this.pipes = pipeline.pipes;
        this.multiBranches = pipeline.multiBranches;
        this.multiMerges = pipeline.multiMerges;
        this.pipelines = pipeline.pipelines;
    }

    @Override
    public T get() throws Exception {
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
    public T apply(S s) throws Exception {
        return execute(s).s;
    }

    public Pipeline findPipeline(String id) {
        if (this.ID.startsWith(id)) {
            return this;
        } else {
            Pipeline pipeline = pipelines.get(id);
            if (pipeline != null) {
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