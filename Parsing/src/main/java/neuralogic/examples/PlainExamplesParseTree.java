package neuralogic.examples;

import neuralogic.grammarParsing.PlainParseTree;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public class PlainExamplesParseTree extends PlainParseTree<NeuralogicParser.ExamplesFileContext> {
    private static final Logger LOG = Logger.getLogger(PlainExamplesParseTree.class.getName());

    public PlainExamplesParseTree(Reader reader) throws IOException {
        super(reader);
    }

    @Override
    public NeuralogicParser.ExamplesFileContext getRoot() {
        return parseTree.examplesFile();
    }
}
