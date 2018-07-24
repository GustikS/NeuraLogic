package building;

import constructs.example.LiftedExample;
import neuralogic.examples.PlainExamplesParseTreeExtractor;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.grammarParsing.PlainParseTree;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ExamplesBuilder extends LogicSourceBuilder<NeuralogicParser.ExamplesFileContext, Stream<LiftedExample>> {
    private static final Logger LOG = Logger.getLogger(ExamplesBuilder.class.getName());

    @Override
    public Stream<LiftedExample> buildFrom(Reader reader) throws IOException {
        return null;
    }

    @Override
    public Stream<LiftedExample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree) {

        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        PlainExamplesParseTreeExtractor examplesParseTreeExtractor = new PlainExamplesParseTreeExtractor(plainGrammarVisitor);
        return buildFrom(parseTree, examplesParseTreeExtractor);
    }

    private Stream<LiftedExample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree, PlainExamplesParseTreeExtractor examplesParseTreeExtractor) {
        Stream<LiftedExample> unlabeledExamples = examplesParseTreeExtractor.getUnlabeledExamples(parseTree.getRoot());
        return unlabeledExamples;
    }
}