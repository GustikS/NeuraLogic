package cz.cvut.fel.ida.logic.parsing.examples;

import org.jetbrains.annotations.NotNull;
import cz.cvut.fel.ida.logic.constructs.Conjunction;
import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.logic.parsing.grammarParsing.GrammarVisitor;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;

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
