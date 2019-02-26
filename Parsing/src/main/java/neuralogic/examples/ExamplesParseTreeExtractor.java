package neuralogic.examples;

import com.sun.istack.internal.NotNull;
import constructs.Conjunction;
import constructs.example.LiftedExample;
import ida.utils.tuples.Pair;
import neuralogic.grammarParsing.GrammarVisitor;
import parsing.antlr.NeuralogicParser;

import java.util.stream.Stream;

public abstract class ExamplesParseTreeExtractor<T extends GrammarVisitor> {

    T visitor;

    protected ExamplesParseTreeExtractor(T v) {
        this.visitor = v;
    }

    public abstract Stream<LiftedExample> getUnlabeledExamples(@NotNull NeuralogicParser.ExamplesFileContext ctx);

    public abstract Stream<Pair<Conjunction, LiftedExample>> getLabeledExamples(@NotNull NeuralogicParser.ExamplesFileContext ctx);

    public abstract Stream<Conjunction> getQueries(@NotNull NeuralogicParser.ExamplesFileContext ctx);
}
