package constructs.building;

import constructs.Conjunction;
import constructs.example.LiftedExample;
import constructs.example.LogicSample;
import constructs.example.ValuedFact;
import ida.utils.tuples.Pair;
import neuralogic.examples.PlainExamplesParseTree;
import neuralogic.examples.PlainExamplesParseTreeExtractor;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import settings.Settings;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * //TODO groundexample when to return? recognizer
 */
public class ExamplesBuilder extends SamplesBuilder<PlainExamplesParseTree, Pair<Conjunction, LiftedExample>> {
    private static final Logger LOG = Logger.getLogger(ExamplesBuilder.class.getName());

    public ExamplesBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public PlainExamplesParseTree parseTreeFrom(Reader reader) {
        try {
            return new PlainExamplesParseTree(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Stream<Pair<Conjunction, LiftedExample>> buildFrom(PlainExamplesParseTree parseTree) {
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        PlainExamplesParseTreeExtractor examplesParseTreeExtractor = new PlainExamplesParseTreeExtractor(plainGrammarVisitor);
        return examplesParseTreeExtractor.getLabeledExamples(parseTree.getRoot());
    }

    @Override
    public Stream<LogicSample> sampleFrom(Pair<Conjunction, LiftedExample> pair) {
        LiftedExample example = pair.s;

        if (pair.r.facts == null || pair.r.facts.size() == 0) {
            LOG.warning("Cannot extract LogicSample(s) without a query provided - emmiting unlabeled LogicSample(s)");
            return Stream.of(createEmptySample(String.valueOf(queryCounter), example));
        } else if (pair.r.facts.size() == 1 && pair.r.facts.get(0).value == null) { // the query literal is a LINK to query file
            ValuedFact query = pair.r.facts.get(0);
            return Stream.of(new LogicSample(null, createQueryAtom(query.literal.toString(), query, example)));
        } else {    // these are not for merging
            String minibatch = String.valueOf(queryCounter);
            return pair.r.facts.stream().map(f -> new LogicSample(f.value.value, createQueryAtom(settings.queriesBatchPrefix + minibatch, f, example)));
        }
    }
    private LogicSample createEmptySample(String id, LiftedExample example) {
        return new LogicSample(null, createQueryAtom(id, null, example));
    }
}