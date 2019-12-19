package parsing.examples;

import com.sun.istack.internal.NotNull;
import constructs.Conjunction;
import constructs.example.LiftedExample;
import parsing.antlr.NeuralogicParser;
import parsing.grammarParsing.PlainGrammarVisitor;
import utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

import static utils.Utilities.zipStreams;

public class PlainExamplesParseTreeExtractor extends ExamplesParseTreeExtractor<PlainGrammarVisitor> {
    private static final Logger LOG = Logger.getLogger(PlainExamplesParseTreeExtractor.class.getName());

    public PlainExamplesParseTreeExtractor(PlainGrammarVisitor v) {
        super(v);
    }

    @Override
    public Stream<LiftedExample> getUnlabeledExamples(@NotNull NeuralogicParser.ExamplesFileContext ctx) {
        PlainGrammarVisitor.LiftedExampleVisitor liftedExampleVisitor = visitor.new LiftedExampleVisitor();
        if (ctx.liftedExample() != null) {
            Stream<LiftedExample> listStream = ctx.liftedExample().stream().map(rule -> rule.accept(liftedExampleVisitor));
            return listStream;
        } else
            LOG.severe("Could not extract any Unlabeled trainExamples");
        return null;
    }

    @Override
    public Stream<Pair<Conjunction, LiftedExample>> getLabeledExamples(@NotNull NeuralogicParser.ExamplesFileContext ctx) {
        if (ctx.label() == null || ctx.label().isEmpty()) {
            return getUnlabeledExamples(ctx).map(ex -> new Pair<>(null, ex));
        } else
            return zipStreams(getQueries(ctx), getUnlabeledExamples(ctx), (q,e) -> createSamplePair(q,e));  //TODO check synchronization of labels and examples - change grammar to single line context object for the pair of (Q,E) and add custom visitor with shared variableFactory
    }

    private Pair<Conjunction, LiftedExample> createSamplePair(Conjunction q, LiftedExample e) {
        LOG.finer("Merging query/label " + q.toString() + " with example " + e);
        return new Pair<>(q,e);
    }

    @Override
    public Stream<Conjunction> getQueries(@NotNull NeuralogicParser.ExamplesFileContext ctx) {
        PlainGrammarVisitor.LabelVisitor labelVisitor = visitor.new LabelVisitor();
        if (ctx.label() != null && !ctx.label().isEmpty())
            return ctx.label().stream().map(line -> line.accept(labelVisitor));
        else
            LOG.severe("Could not extract any trainQueries (weighted facts)");
        return null;
    }
}
