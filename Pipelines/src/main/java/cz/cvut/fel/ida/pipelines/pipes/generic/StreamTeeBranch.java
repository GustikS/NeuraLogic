package cz.cvut.fel.ida.pipelines.pipes.generic;

import cz.cvut.fel.ida.pipelines.Branch;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTeeBranch<S, T extends Stream<S>> extends Branch<T,T,T> {
    private static final Logger LOG = Logger.getLogger(StreamTeeBranch.class.getName());

    public StreamTeeBranch(){
        super("DuplicateBranch");
    }

    public StreamTeeBranch(String id) {
        super(id);
    }

    @Override
    protected Pair<T, T> branch(T outputFromInputPipe) {
        List<S> items = outputFromInputPipe.collect(Collectors.toList());
        return new Pair<>((T) items.stream(), (T) items.stream());
    }
}
