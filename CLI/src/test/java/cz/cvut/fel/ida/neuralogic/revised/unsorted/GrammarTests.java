package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicLexer;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by gusta on 22.2.18.
 */
public class GrammarTests {
    private static final Logger LOG = Logger.getLogger(GrammarTests.class.getName());

    @TestAnnotations.Fast
    public void simpleTemplateTest() throws IOException {
        String resourcePath = Utilities.getResourcePath("simple/family/template.txt");
        NeuralogicLexer lex = new NeuralogicLexer(CharStreams.fromFileName(resourcePath)); // transforms characters into tokens
        CommonTokenStream tokens = new CommonTokenStream(lex); // a token stream
        NeuralogicParser parser = new NeuralogicParser(tokens); // transforms tokens into parse trees
        NeuralogicParser.AtomContext s = parser.templateFile().templateLine(1).lrnnRule().conjunction().atom(1);
        NeuralogicParser.PredicateContext predicate = s.predicate();
        LOG.fine(predicate.getText());
        assertEquals(predicate.getText(), "horse");
    }

}