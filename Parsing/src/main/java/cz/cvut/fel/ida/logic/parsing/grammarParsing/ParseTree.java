package cz.cvut.fel.ida.logic.parsing.grammarParsing;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public abstract class ParseTree <T extends Parser> {
    private static final Logger LOG = Logger.getLogger(ParseTree.class.getName());

    public T parseTree; //todo now drawer for parse tree with DOTTreeGenerator

    public ParseTree(Reader reader) throws IOException {
        Lexer lexer = getLexer(reader);
        TokenStream tokens = new CommonTokenStream(lexer);
        parseTree = getParser(tokens);
    }

    public ParseTree(TokenStream tokens) throws IOException {
        parseTree = getParser(tokens);
    }

    protected abstract T getParser(TokenStream tokens);

    protected abstract Lexer getLexer(Reader reader) throws IOException;

}