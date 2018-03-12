package neuralogic.template;

import neuralogic.ParseTree;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public class XmlParseTree extends ParseTree {
    private static final Logger LOG = Logger.getLogger(XmlParseTree.class.getName());

    public XmlParseTree(Reader reader) throws IOException {
        super(reader);
    }

    @Override
    public NeuralogicParser.Template_fileContext getRoot() {
        return null;
    }
}
