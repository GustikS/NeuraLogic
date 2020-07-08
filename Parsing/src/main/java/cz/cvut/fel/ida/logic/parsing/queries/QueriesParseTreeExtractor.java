package cz.cvut.fel.ida.logic.parsing.queries;

import org.jetbrains.annotations.NotNull;
import cz.cvut.fel.ida.logic.constructs.Conjunction;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;
import cz.cvut.fel.ida.logic.parsing.grammarParsing.GrammarVisitor;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.stream.Stream;

public abstract class QueriesParseTreeExtractor<T extends GrammarVisitor> {

    T visitor;

    QueriesParseTreeExtractor(T v) {
        this.visitor = v;
    }

    public abstract Stream<Conjunction> getQueries(@NotNull NeuralogicParser.QueriesFileContext ctx);

    public abstract Stream<Pair<ValuedFact, Conjunction>> getLabeledQueries(@NotNull NeuralogicParser.QueriesFileContext ctx);
}
