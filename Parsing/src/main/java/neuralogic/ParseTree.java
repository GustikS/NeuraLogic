package neuralogic;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import parsers.neuralogic.NeuralogicLexer;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;

public abstract class ParseTree<T extends ParserRuleContext> {

    protected NeuralogicParser parseTree;

    public ParseTree(Reader reader) throws IOException {
        NeuralogicLexer lexer = new NeuralogicLexer(CharStreams.fromReader(reader));
        TokenStream tokens = new CommonTokenStream(lexer);
        parseTree = new NeuralogicParser(tokens);
    }

    public ParseTree(TokenStream tokens) throws IOException {
        parseTree = new NeuralogicParser(tokens);
    }

    public abstract T getRoot();

}
