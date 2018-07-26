package constructs.building;

import constructs.Conjunction;
import constructs.example.ValuedFact;
import ida.utils.tuples.Pair;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.grammarParsing.PlainParseTree;
import neuralogic.queries.PlainQueriesParseTreeExtractor;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class QueriesBuilder {
    private static final Logger LOG = Logger.getLogger(QueriesBuilder.class.getName());


    public class LabeledQueriesBuilder extends LogicSourceBuilder<NeuralogicParser.QueriesFileContext, Stream<Pair<ValuedFact, Conjunction>>> {
        @Override
        public Stream<Pair<ValuedFact, Conjunction>> buildFrom(Reader reader) throws IOException {
            return null;
        }

        @Override
        public Stream<Pair<ValuedFact, Conjunction>> buildFrom(PlainParseTree<NeuralogicParser.QueriesFileContext> parseTree) {
            PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
            PlainQueriesParseTreeExtractor queriesParseTreeExtractor = new PlainQueriesParseTreeExtractor(plainGrammarVisitor);
            return queriesParseTreeExtractor.getLabeledQueries(parseTree.getRoot());
        }
    }

    public class UnlabeledQueriesBuilder extends LogicSourceBuilder<NeuralogicParser.QueriesFileContext, Stream<Conjunction>> {
        @Override
        public Stream<Conjunction> buildFrom(Reader reader) throws IOException {
            return null;
        }

        @Override
        public Stream<Conjunction> buildFrom(PlainParseTree<NeuralogicParser.QueriesFileContext> parseTree) {
            PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
            PlainQueriesParseTreeExtractor queriesParseTreeExtractor = new PlainQueriesParseTreeExtractor(plainGrammarVisitor);
            return queriesParseTreeExtractor.getQueries(parseTree.getRoot());
        }
    }
}
