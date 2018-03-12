package neuralogic.template;

import neuralogic.ParseTree;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public class PlainTemplateParseTree extends ParseTree<NeuralogicParser.Template_fileContext> {
    private static final Logger LOG = Logger.getLogger(PlainTemplateParseTree.class.getName());

    public PlainTemplateParseTree(Reader reader) throws IOException {
        super(reader);
    }


    @Override
    public NeuralogicParser.Template_fileContext getRoot() {
        return parseTree.template_file();
    }
}