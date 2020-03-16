package cz.cvut.fel.ida.pipelines;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

@Deprecated
public abstract class ParallelPipe<I,O> implements Function<I,O> {
    private static final Logger LOG = Logger.getLogger(ParallelPipe.class.getName());

    public String ID;

    public List<ConnectAfter<I>> inputs;
    public List<ConnectBefore<O>> outputs;

    public List<Pipe<I,O>> pipes;

    public ParallelPipe(String id, int count){
        pipes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            pipes.add(new Pipe<I, O>(id + "Pipe" + count) {
                @Override
                public O apply(I i) {
                    return ParallelPipe.this.apply(i);
                }
            });
        }
    }

    public <T extends ConnectAfter<I>> List<T> connectBefore(List<T> prev){
        if (prev.size() != pipes.size()){
            LOG.severe("ParallelPipe input dimension mismatches with preceding providers!");
        }
        for (int i = 0; i < pipes.size(); i++) {
            prev.get(i).connectAfter(pipes.get(i));
        }
        inputs = (List<ConnectAfter<I>>) prev;
        return prev;
    }

    public <T extends ConnectBefore<O>> List<T> connectAfter(List<T> next){
        if (next.size() != pipes.size()){
            LOG.severe("ParallelPipe output dimension mismatches with preceding providers!");
        }
        for (int i = 0; i < pipes.size(); i++) {
            next.get(i).connectBefore(pipes.get(i));
        }
        outputs = (List<ConnectBefore<O>>) next;
        return next;
    }
}