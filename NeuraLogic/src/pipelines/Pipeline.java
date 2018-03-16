package pipelines;

import ida.utils.tuples.Pair;
import settings.Settings;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Execution pipeline DAG with nodes as Tasks and edges as Pipes.
 * This is a custom implementation with some hacks but also customization,
 * possible, more generic, 3rd party library : https://dexecutor.github.io/
 * <p>
 * Created by gusta on 14.3.17.
 */
public abstract class Pipeline<S, T> {

    Settings settings;
    List<Pipe<S, S>> starts;
    List<Pipe<?, T>> terminals;

    ConcurrentHashMap<String, Branch> branches;
    ConcurrentHashMap<String, Merge> merges;
    ConcurrentHashMap<String, Pipe> pipes;

    /**
     * List of points in the pipeline that need to be called externally, i.e. where streams are terminated.
     */
    ConcurrentLinkedQueue<Executable> executionQueue;

    Pipeline buildFrom(Settings settings) {
        //TODO build different pipeline based on settings
        return new StandardPipeline(settings);
    }

    public List<Pair<String, T>> execute(S source) {
        starts.parallelStream().forEach(start -> start.accept(source));
        while (!executionQueue.isEmpty()) {
            Executable poll = executionQueue.poll();
            poll.run();
        }
        return terminals.stream().map(term -> new Pair<>(term.ID, term.get())).collect(Collectors.toList());
    }

    <I, O> Pipe<I, O> register(Pipe<I, O> p) {
        pipes.put(p.ID, p);
        return p;
    }

    <I, O1, O2> Branch<I, O1, O2> register(Branch<I, O1, O2> b) {
        branches.put(b.ID, b);
        return b;
    }

    <I1, I2, O> Merge<I1, I2, O> register(Merge<I1, I2, O> p) {
        merges.put(p.ID, p);
        executionQueue.add(p);
        return p;
    }
}
