package neuralogic.queries;

import com.sun.istack.internal.NotNull;
import constructs.Conjunction;
import constructs.example.ValuedFact;
import ida.utils.tuples.Pair;
import neuralogic.grammarParsing.GrammarVisitor;
import parsing.antlr.NeuralogicParser;

import java.util.stream.Stream;

public abstract class QueriesParseTreeExtractor<T extends GrammarVisitor> {

    T visitor;

    QueriesParseTreeExtractor(T v) {
        this.visitor = v;
    }

    public abstract Stream<Conjunction> getQueries(@NotNull NeuralogicParser.QueriesFileContext ctx);

    public abstract Stream<Pair<ValuedFact, Conjunction>> getLabeledQueries(@NotNull NeuralogicParser.QueriesFileContext ctx);
}
