package constructs.building;

import constructs.Conjunction;
import constructs.example.AtomQuery;
import constructs.example.GroundExample;
import constructs.example.LiftedExample;
import constructs.example.LogicSample;
import constructs.template.Atom;
import constructs.template.BodyAtom;
import ida.utils.tuples.Pair;
import learning.Query;
import networks.structure.Weight;
import neuralogic.examples.ExamplesParseTreeExtractor;
import neuralogic.examples.PlainExamplesParseTreeExtractor;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.grammarParsing.PlainParseTree;
import neuralogic.queries.PlainQueriesParseTree;
import parsers.neuralogic.NeuralogicParser;
import pipelines.bulding.AbstractPipelineBuilder;
import settings.Settings;
import settings.Source;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * To get labeleb samples from example file (parsable source)
 */
public class SamplesBuilder {
    private static final Logger LOG = Logger.getLogger(SamplesBuilder.class.getName());

    public SamplesBuilder(Settings settings) {
        super(settings);
        //TODO extract from settings what version/format of trainExamples this will be (single vs. multiple files)
    }

    public Stream<LogicSample> buildFrom(Reader reader) throws IOException {
        // Plain text grammar-based version of constructs.building
        return null;
    }

    /**
     * Labels (queries) and examples in 2 separate files
     *
     * @param examplesTree
     * @param queriesTree
     * @return
     */
    public Stream<LogicSample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> examplesTree, PlainParseTree<NeuralogicParser.QueriesFileContext> queriesTree) {
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        ExamplesParseTreeExtractor examplesParseTreeExtractor = new PlainExamplesParseTreeExtractor(plainGrammarVisitor);
        ExamplesBuilder examplesBuilder = new ExamplesBuilder();
        Stream<LiftedExample> groundExampleStream = examplesBuilder.buildFrom(examplesTree);
        QueriesBuilder queriesBuilder = new QueriesBuilder();
        Stream<Query> queryStream = queriesBuilder

    }

    /**
     * Labeled samples from a single file
     *
     * @param parseTree
     * @return
     */
    @Override
    public Stream<LogicSample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree) {
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        ExamplesParseTreeExtractor examplesParseTreeExtractor = new PlainExamplesParseTreeExtractor(plainGrammarVisitor);
        return buildFrom(parseTree, examplesParseTreeExtractor);
    }

    /**
     * Labeled samples from a single file
     *
     * @param parseTree
     * @param examplesParseTreeExtractor
     * @return
     */
    public Stream<LogicSample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree, ExamplesParseTreeExtractor examplesParseTreeExtractor) {
        Stream<Pair<Conjunction, LiftedExample>> labeledSamples = examplesParseTreeExtractor.getLabeledSamples(parseTree.getRoot());
        return labeledSamples.map(weightedRule -> sampleFrom(weightedRule.weight, weightedRule.head, weightedRule.body));
    }

    public LogicSample sampleFrom(Weight value, Atom query, List<BodyAtom> example) {
        AtomQuery atomQuery = new AtomQuery(query, new GroundExample(example));
        return new LogicSample(atomQuery, value.value);
    }

    /**
     * Terminal operation - necessary to process the full streams to build a Map!
     *
     * @param examples
     * @param queries
     * @return
     */
    public Stream<LogicSample> combine(Stream<GroundExample> examples, Stream<Query> queries) {

    }

    public Stream<LogicSample> buildFrom(PlainQueriesParseTree queriesParseTree) {
        QueriesBuilder queriesBuilder = new QueriesBuilder();
    }
}