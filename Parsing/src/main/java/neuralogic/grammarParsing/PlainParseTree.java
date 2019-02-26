package neuralogic.grammarParsing;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import parsing.antlr.NeuralogicLexer;
import parsing.antlr.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public abstract class PlainParseTree<T extends ParserRuleContext> extends ParseTree<NeuralogicParser> {
    private static final Logger LOG = Logger.getLogger(PlainParseTree.class.getName());

    public PlainParseTree(Reader reader) throws IOException {
        super(reader);
    }

    public PlainParseTree(TokenStream tokens) throws IOException {
        super(tokens);
    }

    protected NeuralogicParser getParser(TokenStream tokens) {
        return new NeuralogicParser(tokens);
    }

    protected NeuralogicLexer getLexer(Reader reader) throws IOException {
        return new NeuralogicLexer(CharStreams.fromReader(reader));
    }

    public abstract T getRoot();

    public boolean isEmpty(){
        boolean empty = getRoot().isEmpty();
        if (empty) {
            LOG.warning("Parse tree from file is EMPTY! (i.e. some effectively empty file was provided)");
        }
        return empty;
    }

}