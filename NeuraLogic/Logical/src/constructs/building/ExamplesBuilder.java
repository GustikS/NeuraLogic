package constructs.building;

import constructs.Conjunction;
import constructs.example.LiftedExample;
import ida.utils.tuples.Pair;
import neuralogic.examples.PlainExamplesParseTreeExtractor;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.grammarParsing.PlainParseTree;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ExamplesBuilder {
    //TODO groundexample when to return? recognizer
    private static final Logger LOG = Logger.getLogger(ExamplesBuilder.class.getName());


    public class UnlabeledExamplesBuilder extends LogicSourceBuilder<NeuralogicParser.ExamplesFileContext, Stream<LiftedExample>> {
        @Override
        public Stream<LiftedExample> buildFrom(Reader reader) throws IOException {
            return null;
        }

        @Override
        public Stream<LiftedExample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree) {
            PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
            PlainExamplesParseTreeExtractor examplesParseTreeExtractor = new PlainExamplesParseTreeExtractor(plainGrammarVisitor);
            return examplesParseTreeExtractor.getUnlabeledExamples(parseTree.getRoot());
        }
    }

    public class LabeledExamplesBuilder extends LogicSourceBuilder<NeuralogicParser.ExamplesFileContext, Stream<Pair<Conjunction,LiftedExample>>> {

        @Override
        public Stream<Pair<Conjunction, LiftedExample>> buildFrom(Reader reader) throws IOException {
            return null;
        }

        @Override
        public Stream<Pair<Conjunction, LiftedExample>> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree) throws IOException {
            PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
            PlainExamplesParseTreeExtractor examplesParseTreeExtractor = new PlainExamplesParseTreeExtractor(plainGrammarVisitor);
            return examplesParseTreeExtractor.getLabeledSamples(parseTree.getRoot());
        }
    }
}