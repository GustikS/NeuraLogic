package cz.cvut.fel.ida.neuralogic.cli.benchmarks.dummy;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(3)
public class SearchBenchmark {

    @State(Scope.Thread)
    public static class SearchState {
        public String text = "abcdefghijklmnopqrstuvwxyz";
        public String search = "l";
        public char searchChar = 'l';
    }

    @Benchmark
    public int testIndexOf(SearchState state) {
        return state.text.indexOf(state.search);
    }

    @Benchmark
    public int testIndexOfChar(SearchState state) {
        return state.text.indexOf(state.searchChar);
    }

    @Benchmark
    public boolean testContains(SearchState state) {
        return state.text.contains(state.search);
    }
}