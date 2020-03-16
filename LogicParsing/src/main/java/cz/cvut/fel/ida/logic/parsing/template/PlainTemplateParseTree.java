package cz.cvut.fel.ida.logic.parsing.template;

import cz.cvut.fel.ida.logic.parsing.grammarParsing.PlainParseTree;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;

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