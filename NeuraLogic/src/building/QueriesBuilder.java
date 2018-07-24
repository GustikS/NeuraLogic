package building;

import learning.Query;
import neuralogic.grammarParsing.PlainParseTree;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class QueriesBuilder extends LogicSourceBuilder<NeuralogicParser.QueriesFileContext, Stream<Query>> {
    private static final Logger LOG = Logger.getLogger(QueriesBuilder.class.getName());

    @Override
    public Stream<Query> buildFrom(Reader reader) throws IOException {
        return null;
    }

    @Override
    public Stream<Query> buildFrom(PlainParseTree<NeuralogicParser.QueriesFileContext> parseTree) {
        //TODO
        return null;
    }
}
