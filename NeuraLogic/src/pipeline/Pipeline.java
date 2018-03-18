package pipeline;

import ida.utils.tuples.Pair;
import pipeline.pipelines.TrainingPipeline;
import settings.Settings;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Execution pipeline DAG with nodes as Tasks and edges as Pipes. It is your responsibility to create and connect the Pipes
 * correctly, and register the Executable (non-streaming) Pipes in topological order for scheduling.
 *
 * This is a custom implementation with some hacks but also customization,
 * possible, more generic, 3rd party library : https://dexecutor.github.io/
 * <p>
 * Created by gusta on 14.3.17.
 */
public abstract class Pipeline<S, T> {

    protected Settings settings;
    protected List<Pipe<S, S>> starts;
    protected List<Pipe<?, T>> terminals;

    ConcurrentHashMap<String, Branch> branches;
    ConcurrentHashMap<String, Merge> merges;
    ConcurrentHashMap<String, Pipe> pipes;

    /**
     * List of points in the pipeline that need to be called externally, i.e. where streams are terminated.
     */
    //ConcurrentLinkedQueue<Executable> executionQueue;

    Pipeline buildFrom(Settings settings) {
        //TODO build different pipeline based on settings
        return new TrainingPipeline(settings);
    }

    public List<Pair<String, T>> execute(S source) {
        starts.parallelStream().forEach(start -> start.accept(source));
        /*while (!executionQueue.isEmpty()) {
            Executable poll = executionQueue.poll();
            poll.run();
        }*/
        return terminals.stream().map(term -> new Pair<>(term.ID, term.get())).collect(Collectors.toList());
    }

    protected <I, O> Pipe<I, O> register(Pipe<I, O> p) {
        pipes.put(p.ID, p);
        return p;
    }

    protected <I, O1, O2> Branch<I, O1, O2> register(Branch<I, O1, O2> b) {
        branches.put(b.ID, b);
        return b;
    }

    protected <I1, I2, O> Merge<I1, I2, O> register(Merge<I1, I2, O> p) {
        merges.put(p.ID, p);
        //executionQueue.add(p);
        return p;
    }
}