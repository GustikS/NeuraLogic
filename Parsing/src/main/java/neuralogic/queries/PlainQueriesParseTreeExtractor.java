package neuralogic.queries;

import constructs.Conjunction;
import constructs.example.ValuedFact;
import ida.utils.tuples.Pair;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import parsers.neuralogic.NeuralogicParser;

import java.util.logging.Logger;
import java.util.stream.Stream;

import static utils.Utilities.zipStreams;

public class PlainQueriesParseTreeExtractor extends QueriesParseTreeExtractor<PlainGrammarVisitor> {
    private static final Logger LOG = Logger.getLogger(PlainQueriesParseTreeExtractor.class.getName());

    public PlainQueriesParseTreeExtractor(PlainGrammarVisitor v) {
        super(v);
    }

    @Override
    public Stream<Conjunction> getQueries(NeuralogicParser.QueriesFileContext ctx) {
        PlainGrammarVisitor.FactConjunctionVisitor factConjunctionVisitor = visitor.new FactConjunctionVisitor();
        if (ctx.conjunction() != null) {
            Stream<Conjunction> labelStream = ctx.conjunction().stream().map(line -> line.accept(factConjunctionVisitor));
            return labelStream;
        } else
            LOG.severe("Could not extract any queries");
        return null;
    }

    @Override
    public Stream<Pair<ValuedFact, Conjunction>> getLabeledQueries(NeuralogicParser.QueriesFileContext ctx) {
        PlainGrammarVisitor.FactVisitor factVisitor = visitor.new FactVisitor();
        PlainGrammarVisitor.FactConjunctionVisitor factConjunctionVisitor = visitor.new FactConjunctionVisitor();
        if (ctx.atom() != null) {
            Stream<ValuedFact> labelStream = ctx.atom().stream().map(atom -> atom.accept(factVisitor));
            Stream<Conjunction> queriesStream = ctx.conjunction().stream().map(line -> line.accept(factConjunctionVisitor));
            return zipStreams(labelStream, queriesStream, (lab, query) -> new Pair(lab, query));

        } else
            LOG.severe("Could not extract any labeled queries");
        return null;
    }

}
