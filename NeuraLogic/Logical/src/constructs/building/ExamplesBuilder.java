package constructs.building;

import constructs.Conjunction;
import constructs.example.LiftedExample;
import constructs.example.LogicSample;
import constructs.example.ValuedFact;
import parsing.antlr.NeuralogicParser;
import parsing.examples.PlainExamplesParseTree;
import parsing.examples.PlainExamplesParseTreeExtractor;
import parsing.grammarParsing.PlainGrammarVisitor;
import settings.Settings;
import utils.generic.Pair;

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

    /**
     * We return getLabeledExamples by default, even if the labels are to be null
     * @param parseTree
     * @return
     */
    @Override
    public Stream<Pair<Conjunction, LiftedExample>> buildFrom(PlainExamplesParseTree parseTree) {
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        PlainExamplesParseTreeExtractor examplesParseTreeExtractor = new PlainExamplesParseTreeExtractor(plainGrammarVisitor);
        NeuralogicParser.ExamplesFileContext examplesFileContext = parseTree.getRoot();
        inferInputFormatSettings(examplesFileContext);
        return examplesParseTreeExtractor.getLabeledExamples(examplesFileContext);
    }

    @Override
    public Stream<LogicSample> sampleFrom(Pair<Conjunction, LiftedExample> pair) {
        LiftedExample example = pair.s;

        if (pair.r == null || pair.r.facts == null || pair.r.facts.size() == 0) {
            //LOG.finest("Unlabeled input examples detected - queries must be provided in a separate file");
            return Stream.of(createUnlabeledSample(String.valueOf(queryCounter), example));
        } else if ((pair.r.facts.size() == 1) && (pair.r.facts.get(0).getValue() == null)) { // the query literal is a LINK to query file
            ValuedFact query = pair.r.facts.get(0);
            return Stream.of(new LogicSample(null, createQueryAtom(query.literal.toString(), query, example)));
        } else {    // these are not for merging
            String minibatch = String.valueOf(queryCounter);
            return pair.r.facts.stream().map(f -> new LogicSample(f.getValue(), createQueryAtom(settings.queriesBatchPrefix + minibatch, f, example)));
        }
    }
    private LogicSample createUnlabeledSample(String id, LiftedExample example) {
        return new LogicSample(null, createQueryAtom(id, null, example));
    }

    private void inferInputFormatSettings(NeuralogicParser.ExamplesFileContext examplesFileContext) {   //todo next move these at the beginning - before pipeline creation!
        if (examplesFileContext.liftedExample().size() == 0){
            LOG.warning("There are no examples in the example source (file)!");
        } else if (examplesFileContext.liftedExample().size() == 1){
            LOG.info("Detecting exactly 1 (big) example in the examples source (file), switching to knowledge-base mode.");
            settings.groundingMode = Settings.GroundingMode.GLOBAL;
        } else {
            LOG.info("Detecting multiple individual examples in the examples source (file), assuming independent graph mode.");
            settings.queriesAlignedWithExamples = true;
            settings.groundingMode = Settings.GroundingMode.NORMAL;
        }

        if (examplesFileContext.label() != null && !examplesFileContext.label().isEmpty()){
            LOG.info("Detecting examples to have ids/queries with them.");
        }
    }
}