package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.logic.constructs.building.TemplateBuilder;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicBaseListener;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicLexer;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;
import cz.cvut.fel.ida.logic.parsing.grammarParsing.PlainGrammarVisitor;
import cz.cvut.fel.ida.logic.parsing.template.PlainTemplateParseTreeExtractor;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by gusta on 27.2.18.
 */
public class ParsingTests {
    private static final Logger LOG = Logger.getLogger(ParsingTests.class.getName());

    @TestAnnotations.Fast
    public void listenerRunTest() throws IOException {
        NeuralogicLexer lex = new NeuralogicLexer(CharStreams.fromFileName("../Resources/datasets/simple/family/template.txt")); // transforms characters into tokens
        CommonTokenStream tokens = new CommonTokenStream(lex); // a token stream
        NeuralogicParser parser = new NeuralogicParser(tokens); // transforms tokens into parse trees
        parser.setBuildParseTree(true);

        NeuralogicParser.TemplateFileContext templateFileContext = parser.templateFile();

        ParseTreeWalker walker = new ParseTreeWalker();

        NeuralogicBaseListener neuralogicBaseListener = new NeuralogicBaseListener();

        walker.walk(neuralogicBaseListener, templateFileContext);
    }

    @TestAnnotations.Fast
    public void visitorrRunTest() throws IOException {
        NeuralogicLexer lex = new NeuralogicLexer(CharStreams.fromFileName("../Resources/datasets/simple/family/template.txt")); // transforms characters into tokens
        CommonTokenStream tokens = new CommonTokenStream(lex); // a token stream
        NeuralogicParser parser = new NeuralogicParser(tokens); // transforms tokens into parse trees
        parser.setBuildParseTree(true);

        NeuralogicParser.TemplateFileContext template_fileContext = parser.templateFile();

        PlainTemplateParseTreeExtractor plainTemplateParseTreeExtractor = new PlainTemplateParseTreeExtractor(new PlainGrammarVisitor(new TemplateBuilder(new Settings())));
        List<WeightedRule> weightedRules = plainTemplateParseTreeExtractor.getWeightedRules(template_fileContext);
        assertNotNull(weightedRules);
        assertEquals(weightedRules.size(), 3);
        for (WeightedRule weightedRule : weightedRules) {
            LOG.fine(weightedRule.getOriginalString());
        }
    }

}