package constructs.building;

import constructs.Conjunction;
import constructs.example.LogicSample;
import constructs.example.ValuedFact;
import ida.utils.tuples.Pair;
import networks.computation.evaluation.values.ScalarValue;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.queries.PlainQueriesParseTree;
import neuralogic.queries.PlainQueriesParseTreeExtractor;
import settings.Settings;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class QueriesBuilder extends SamplesBuilder<PlainQueriesParseTree, Pair<ValuedFact, Conjunction>> {
    private static final Logger LOG = Logger.getLogger(QueriesBuilder.class.getName());

    public QueriesBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public PlainQueriesParseTree parseTreeFrom(Reader reader) {
        try {
            return new PlainQueriesParseTree(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Stream<Pair<ValuedFact, Conjunction>> buildFrom(PlainQueriesParseTree parseTree) {
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        PlainQueriesParseTreeExtractor queriesParseTreeExtractor = new PlainQueriesParseTreeExtractor(plainGrammarVisitor);
        return queriesParseTreeExtractor.getLabeledQueries(parseTree.getRoot());
    }

    /**
     * Stream of samples belonging to a single example (subset of the whole sample set)
     * //todo test measure performance of this (and possibly replace with 2 methods, one without creating stream when not necessary)
     *
     * @param pair
     * @return
     */
    @Override
    public Stream<LogicSample> sampleFrom(Pair<ValuedFact, Conjunction> pair) {

        if (pair.s.facts == null || pair.s.facts.size() == 0){
            LOG.severe("Cannot extract LogicSample(s) without a query provided!");
            return Stream.empty();
        }
        Stream<ValuedFact> queries = pair.s.facts.stream();

        if (pair.r != null) {   // labeled query(ies)
            String id = pair.r.literal.toString();
            if (pair.r.getValue() != null) { // has importance set
                if (pair.r.getValue() instanceof ScalarValue) {
                    final double importance = ((ScalarValue) pair.r.getValue()).value;
                    return queries.map(f -> new LogicSample(f.getValue(), createQueryAtom(id, importance, f)));
                } else {
                    LOG.warning("Query with non-scalar target value not supported (yet)");
                    return queries.map(f -> new LogicSample(f.getValue(), createQueryAtom(id, f)));
                }
            } else {
                return queries.map(f -> new LogicSample(f.getValue(), createQueryAtom(id, f)));
            }
        }
        String minibatch = String.valueOf(queryCounter);
        return queries.map(f -> new LogicSample(f.getValue(), createQueryAtom(minibatch, f)));
    }
}