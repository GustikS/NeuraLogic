package neuralogic.examples;

import com.sun.istack.internal.NotNull;
import constructs.example.WeightedFact;
import constructs.template.BodyAtom;
import constructs.template.WeightedRule;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import parsers.neuralogic.NeuralogicParser;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlainExamplesParseTreeExtractor extends ExamplesParseTreeExtractor<PlainGrammarVisitor> {
    private static final Logger LOG = Logger.getLogger(PlainExamplesParseTreeExtractor.class.getName());

    PlainExamplesParseTreeExtractor(PlainGrammarVisitor v) {
        super(v);
    }

    @Override
    public Stream<List<BodyAtom>> getUnlabeledExamples(@NotNull NeuralogicParser.Examples_fileContext ctx) {
        PlainGrammarVisitor.ConjunctionVisitor conjunctionVisitor = visitor.new ConjunctionVisitor();
        if (ctx.conjunction() != null) {
            Stream<List<BodyAtom>> listStream = ctx.conjunction().stream().map(rule -> rule.accept(conjunctionVisitor));
            return listStream;
        } else
            LOG.severe("Could not extract any Unlabeled examples (conjunctions of atoms)");
        return null;
    }

    @Override
    public List<WeightedFact> getOneBigExample(@NotNull NeuralogicParser.Examples_fileContext ctx) {
        PlainGrammarVisitor.FactVisitor factVisitor = visitor.new FactVisitor();
        if (ctx.fact() != null)
            return ctx.fact().stream().map(fact -> fact.accept(factVisitor)).collect(Collectors.toList());
        else
            LOG.severe("Could not extract any weighted facts");
        return null;
    }

    @Override
    public Stream<WeightedRule> getLabeledSamples(@NotNull NeuralogicParser.Examples_fileContext ctx) {
        PlainGrammarVisitor.RuleLineVisitor ruleLineVisitor = visitor.new RuleLineVisitor();
        if (ctx.lrnn_rule() != null)
            return ctx.lrnn_rule().stream().map(rule -> rule.accept(ruleLineVisitor));
        else
            LOG.severe("Could not extract any Labeled Samples (weighted rules)");
        return null;
    }

    @Override
    public Stream<WeightedFact> getQueries(@NotNull NeuralogicParser.Queries_fileContext ctx) {
        PlainGrammarVisitor.FactVisitor factVisitor = visitor.new FactVisitor();
        if (ctx.fact() != null)
            return ctx.fact().stream().map(fact -> fact.accept(factVisitor));
        else
            LOG.severe("Could not extract any queries (weighted facts)");
        return null;
    }
}
