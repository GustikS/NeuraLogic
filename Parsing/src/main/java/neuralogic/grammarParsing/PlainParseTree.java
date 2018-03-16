package neuralogic.grammarParsing;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import parsers.neuralogic.NeuralogicLexer;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;

public abstract class PlainParseTree<T extends ParserRuleContext> extends ParseTree<NeuralogicParser> {

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

}