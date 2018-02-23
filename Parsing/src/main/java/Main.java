import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parsers.Hello.HelloLexer;
import parsers.Hello.HelloParser;
import parsers.Test.TestLexer;
import parsers.Test.TestParser;

import java.io.IOException;

/**
 * Created by gusta on 22.2.18.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        testTest("Hello Joshua!");
        helloTest("Hello world");
    }
    public static void testTest(String args) throws IOException {
        TestLexer lex = new TestLexer(CharStreams.fromString(args)); // transforms characters into tokens
        CommonTokenStream tokens = new CommonTokenStream(lex); // a token stream
        TestParser parser = new TestParser(tokens); // transforms tokens into parse trees
        String s = parser.main().name().getText();
        System.out.println(s);
    }

    public static void helloTest(String args) throws IOException {
        HelloLexer lex = new HelloLexer(CharStreams.fromString(args)); // transforms characters into tokens
        CommonTokenStream tokens = new CommonTokenStream(lex); // a token stream
        HelloParser parser = new HelloParser(tokens); // transforms tokens into parse trees
        String s = parser.r().ID().getText();
        System.out.println(s);
    }
}