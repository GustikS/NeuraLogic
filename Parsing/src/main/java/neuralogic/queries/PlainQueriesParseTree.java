package neuralogic.queries;

import neuralogic.grammarParsing.PlainParseTree;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public class PlainQueriesParseTree extends PlainParseTree<NeuralogicParser.QueriesFileContext> {
    private static final Logger LOG = Logger.getLogger(PlainQueriesParseTree.class.getName());

    public PlainQueriesParseTree(Reader reader) throws IOException {
        super(reader);
    }

    @Override
    public NeuralogicParser.QueriesFileContext getRoot() {
        return parseTree.queriesFile();
    }
}
