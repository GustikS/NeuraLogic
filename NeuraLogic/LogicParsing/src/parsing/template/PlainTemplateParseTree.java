package parsing.template;

import parsing.grammarParsing.PlainParseTree;
import parsing.antlr.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public class PlainTemplateParseTree extends PlainParseTree<NeuralogicParser.TemplateFileContext> {
    private static final Logger LOG = Logger.getLogger(PlainTemplateParseTree.class.getName());

    public PlainTemplateParseTree(Reader reader) throws IOException {
        super(reader);
    }


    @Override
    public NeuralogicParser.TemplateFileContext getRoot() {
        return parseTree.templateFile();
    }
}