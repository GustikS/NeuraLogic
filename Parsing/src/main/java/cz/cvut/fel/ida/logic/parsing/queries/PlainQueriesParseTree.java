package cz.cvut.fel.ida.logic.parsing.queries;

import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;
import cz.cvut.fel.ida.logic.parsing.grammarParsing.PlainParseTree;

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
