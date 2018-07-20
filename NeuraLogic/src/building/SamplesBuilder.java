package building;

import constructs.example.AtomQuery;
import constructs.example.GroundExample;
import constructs.template.Atom;
import constructs.template.BodyAtom;
import constructs.template.WeightedRule;
import learning.LearningSample;
import networks.structure.Weight;
import neuralogic.examples.ExamplesParseTreeExtractor;
import neuralogic.examples.PlainExamplesParseTree;
import neuralogic.examples.PlainExamplesParseTreeExtractor;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.grammarParsing.PlainParseTree;
import parsers.neuralogic.NeuralogicParser;
import settings.Settings;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SamplesBuilder extends LogicSourceBuilder<NeuralogicParser.ExamplesFileContext, Stream<LearningSample>> {
    private static final Logger LOG = Logger.getLogger(SamplesBuilder.class.getName());

    public SamplesBuilder(Settings settings) {

        this.settings = settings;
        //TODO extract from settings what version/format of trainExamples this will be (single vs. multiple files)
    }

    @Override
    public Stream<LearningSample> buildFrom(Reader reader) throws IOException {
        // Plain text grammar-based version of building
        PlainExamplesParseTree parseTree = new PlainExamplesParseTree(reader);
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        ExamplesParseTreeExtractor examplesParseTreeExtractor = new PlainExamplesParseTreeExtractor(plainGrammarVisitor);

        Stream<LearningSample> sampleStream;
        if (parseTree.getRoot().lrnn_rule() != null) {
            sampleStream = buildFrom(parseTree, examplesParseTreeExtractor);
        } else if (parseTree.getRoot().conjunction() != null) {

        }
    }

    @Override
    public Stream<LearningSample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree) throws IOException {
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        ExamplesParseTreeExtractor examplesParseTreeExtractor = new PlainExamplesParseTreeExtractor(plainGrammarVisitor);
        return buildFrom(parseTree,examplesParseTreeExtractor);
    }

    public Stream<LearningSample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree, ExamplesParseTreeExtractor examplesParseTreeExtractor) {
        Stream<WeightedRule> labeledSamples = examplesParseTreeExtractor.getLabeledSamples(parseTree.getRoot());
        return labeledSamples.map(weightedRule -> sampleFrom(weightedRule.weight, weightedRule.head, weightedRule.body));
    }

    public LearningSample sampleFrom(Weight value, Atom query, List<BodyAtom> example) {
        AtomQuery atomQuery = new AtomQuery(query, new GroundExample(example));
        return new LearningSample(atomQuery, value.value);
    }
}