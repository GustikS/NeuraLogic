package neuralogic.examples;

import neuralogic.grammarParsing.PlainParseTree;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public class PlainQueriesParseTree extends PlainParseTree<NeuralogicParser.Queries_fileContext> {
    private static final Logger LOG = Logger.getLogger(PlainQueriesParseTree.class.getName());

    public PlainQueriesParseTree(Reader reader) throws IOException {
        super(reader);
    }

    @Override
    public NeuralogicParser.Queries_fileContext getRoot() {
        return parseTree.queries_file();
    }
}
