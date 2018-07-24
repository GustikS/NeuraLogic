package building;

import constructs.Conjunction;
import constructs.example.GroundExample;
import constructs.example.LiftedExample;
import ida.utils.tuples.Pair;
import neuralogic.examples.ExamplesParseTreeExtractor;
import neuralogic.examples.PlainExamplesParseTreeExtractor;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.grammarParsing.PlainParseTree;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ExamplesBuilder extends LogicSourceBuilder<NeuralogicParser.ExamplesFileContext, Stream<GroundExample>> {
    private static final Logger LOG = Logger.getLogger(ExamplesBuilder.class.getName());


    @Override
    public Stream<GroundExample> buildFrom(Reader reader) throws IOException {
        return null;
    }

    @Override
    public Stream<GroundExample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree) {

        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        ExamplesParseTreeExtractor examplesParseTreeExtractor = new PlainExamplesParseTreeExtractor(plainGrammarVisitor);
        return buildFrom(parseTree, examplesParseTreeExtractor);
    }

    private Stream<GroundExample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree, ExamplesParseTreeExtractor examplesParseTreeExtractor) {
        Stream<Pair<Conjunction, LiftedExample>> labeledSamples = examplesParseTreeExtractor.getLabeledSamples(parseTree.getRoot());
        labeledSamples.peek()

    }
}