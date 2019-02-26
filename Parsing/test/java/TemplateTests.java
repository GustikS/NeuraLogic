import neuralogic.TemplateListener;
import neuralogic.TemplateVisitor_old;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import parsers.antlr.neuralogicLexer;
import parsers.antlr.neuralogicParser;

import java.io.IOException;

/**
 * Created by gusta on 27.2.18.
 */
public class TemplateTests {

    @Test
    public void listenerRunTest() throws IOException {
        neuralogicLexer lex = new neuralogicLexer(CharStreams.fromFileName("src/main/resources/test_template")); // transforms characters into tokens
        CommonTokenStream tokens = new CommonTokenStream(lex); // a token stream
        neuralogicParser parser = new neuralogicParser(tokens); // transforms tokens into parse trees
        parser.setBuildParseTree(true);

        neuralogicParser.Template_fileContext template_fileContext = parser.template_file();

        ParseTreeWalker walker = new ParseTreeWalker();
        TemplateListener listener = new TemplateListener();
        walker.walk(listener, template_fileContext);
    }

    @Test
    public void visitorrRunTest() throws IOException {
        neuralogicLexer lex = new neuralogicLexer(CharStreams.fromFileName("src/main/resources/test_template")); // transforms characters into tokens
        CommonTokenStream tokens = new CommonTokenStream(lex); // a token stream
        neuralogicParser parser = new neuralogicParser(tokens); // transforms tokens into parse trees
        parser.setBuildParseTree(true);

        neuralogicParser.Template_fileContext template_fileContext = parser.template_file();

        TemplateVisitor_old visitor = new TemplateVisitor_old();
        visitor.visit(template_fileContext);
    }
}