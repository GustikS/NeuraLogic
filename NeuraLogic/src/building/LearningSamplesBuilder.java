package building;

import constructs.example.AtomQuery;
import constructs.example.GroundExample;
import constructs.template.Atom;
import constructs.template.BodyAtom;
import constructs.template.WeightedRule;
import learning.LearningSample;
import learning.Query;
import networks.structure.Weight;
import neuralogic.examples.ExamplesParseTreeExtractor;
import neuralogic.examples.PlainExamplesParseTree;
import neuralogic.examples.PlainExamplesParseTreeExtractor;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import settings.Settings;
import settings.Sources;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class LearningSamplesBuilder extends LogicBuilder<Stream<LearningSample>> {
    private static final Logger LOG = Logger.getLogger(LearningSamplesBuilder.class.getName());

    public LearningSamplesBuilder(Settings settings) {

        this.settings = settings;
        //TODO extract from settings what version/format of trainExamples this will be (single vs. multiple files)
    }

    @Override
    public Stream<LearningSample> buildFrom(Reader reader) throws IOException {
        //TODO switch to different Parsers and Extractors based on file types/structures/settings

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
    public Stream<LearningSample> buildFrom(Sources sources) throws IOException {
        sources.trainExamplesParseTree
    }

    public Stream<LearningSample> buildFrom(PlainExamplesParseTree parseTree, ExamplesParseTreeExtractor examplesParseTreeExtractor) {
        Stream<WeightedRule> labeledSamples = examplesParseTreeExtractor.getLabeledSamples(parseTree.getRoot());
        return labeledSamples.map(weightedRule -> sampleFrom(weightedRule.weight, weightedRule.head, weightedRule.body));
    }

    public LearningSample sampleFrom(Weight value, Atom query, List<BodyAtom> example) {
        AtomQuery atomQuery = new AtomQuery(query, new GroundExample(example));
        return new LearningSample(atomQuery, value.value);
    }


    /**
     * Parsing separate example facts
     */
    public class ExampleBuilder extends LogicBuilder<Stream<GroundExample>> {

        @Override
        public Stream<GroundExample> buildFrom(Reader reader) throws IOException {
            return null;
        }

        @Override
        public Stream<GroundExample> buildFrom(Sources sources) throws IOException {
            return null;
        }
    }

    /**
     * Parsing separate query-labels
     */
    public class QueryBuilder extends LogicBuilder<Stream<Query>> {

        @Override
        public Stream<Query> buildFrom(Reader reader) throws IOException {
            return null;
        }

        @Override
        public Stream<Query> buildFrom(Sources sources) throws IOException {
            return null;
        }
    }
}
