package cz.cvut.fel.ida.logic.parsing.queries;

import cz.cvut.fel.ida.logic.constructs.Conjunction;
import cz.cvut.fel.ida.logic.constructs.building.factories.VariableFactory;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.Utilities;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;
import cz.cvut.fel.ida.logic.parsing.grammarParsing.PlainGrammarVisitor;

import java.util.logging.Logger;
import java.util.stream.Stream;

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
        VariableFactory variableFactory = new VariableFactory();
        PlainGrammarVisitor.FactVisitor factVisitor = visitor.new FactVisitor();
        factVisitor.variableFactory = variableFactory;
        PlainGrammarVisitor.FactConjunctionVisitor factConjunctionVisitor = visitor.new FactConjunctionVisitor();
        factConjunctionVisitor.variableFactory = variableFactory;
        if (ctx.conjunction() != null) {
            Stream<Conjunction> queriesStream = ctx.conjunction().stream().map(
                    line -> line.accept(factConjunctionVisitor)
            );
            if (ctx.atom() != null && ctx.atom().size() > 0) {
//                LOG.info("Detecting queries to have ids with them.");
                Stream<ValuedFact> labelStream = ctx.atom().stream().map(atom -> atom.accept(factVisitor));
                return Utilities.zipStreams(labelStream, queriesStream, (lab, query) -> new Pair(lab, query));    //TODO do not zip, create a single line context object (same as in labeled Examples)
            } else {
//                LOG.info("Detecting that the provided queries have no ids with them.");
                return queriesStream.map(q -> new Pair(null, q));
            }
        } else
            LOG.severe("Could not extract any queries");
        return null;
    }
}