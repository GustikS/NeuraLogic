import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import parsers.hello_world.hello_worldLexer;
import parsers.hello_world.hello_worldParser;
import parsers.neuralogic.NeuralogicLexer;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by gusta on 22.2.18.
 */
public class GrammarTests {

    @Test
    public void helloWorldTest() throws IOException {
        hello_worldLexer lex = new hello_worldLexer(CharStreams.fromFileName("src/main/resources/hello_test_input")); // transforms characters into tokens
        CommonTokenStream tokens = new CommonTokenStream(lex); // a token stream
        hello_worldParser parser = new hello_worldParser(tokens); // transforms tokens into parse trees
        String s = parser.rule2().WORD().toString();
        assertEquals(s, "[w, o, r, l, d]");
        //System.out.println(s);
    }

    @Test
    public void simpleTemplateTest() throws IOException {
        NeuralogicLexer lex = new NeuralogicLexer(CharStreams.fromFileName("src/main/resources/test_template")); // transforms characters into tokens
        CommonTokenStream tokens = new CommonTokenStream(lex); // a token stream
        NeuralogicParser parser = new NeuralogicParser(tokens); // transforms tokens into parse trees
        String s = parser.template_file().template_line(1).lrnn_rule().conjunction().atom(0).weight().ATOMIC_NAME().getText();
        assertEquals(s, "id_2");
        //System.out.println(s);
    }

}