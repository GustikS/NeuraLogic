package neuralogic.examples;

import com.sun.istack.internal.NotNull;
import constructs.example.WeightedFact;
import constructs.template.BodyAtom;
import constructs.template.WeightedRule;
import neuralogic.grammarVisitors.GrammarVisitor;
import parsers.neuralogic.NeuralogicParser;

import java.util.List;
import java.util.stream.Stream;

public abstract class ExamplesParseTreeExtractor<T extends GrammarVisitor> {

    T visitor;

    ExamplesParseTreeExtractor(T v) {
        this.visitor = v;
    }

    public abstract Stream<List<BodyAtom>> getUnlabeledExamples(@NotNull NeuralogicParser.Examples_fileContext ctx);
    public abstract List<WeightedFact> getOneBigExample(@NotNull NeuralogicParser.Examples_fileContext ctx);

    public abstract Stream<WeightedRule> getLabeledSamples(@NotNull NeuralogicParser.Examples_fileContext ctx);

    public abstract Stream<WeightedFact> getQueries(@NotNull NeuralogicParser.Queries_fileContext ctx);
}