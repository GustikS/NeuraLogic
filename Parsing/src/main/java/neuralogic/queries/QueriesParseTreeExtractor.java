package neuralogic.queries;

import com.sun.istack.internal.NotNull;
import constructs.Conjunction;
import constructs.template.Atom;
import ida.utils.tuples.Pair;
import neuralogic.grammarParsing.GrammarVisitor;
import parsers.neuralogic.NeuralogicParser;

import java.util.stream.Stream;

public abstract class QueriesParseTreeExtractor<T extends GrammarVisitor> {

    T visitor;

    QueriesParseTreeExtractor(T v) {
        this.visitor = v;
    }

    public abstract Stream<Conjunction> getQueries(@NotNull NeuralogicParser.QueriesFileContext ctx);

    public abstract Stream<Pair<Atom, Conjunction>> getLabeledQueries(@NotNull NeuralogicParser.QueriesFileContext ctx);
}
