package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.learning.results.ClassificationResults;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicLexer;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.time.Duration;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SparseCoding {
    private static final Logger LOG = Logger.getLogger(SparseCoding.class.getName());

    @TestAnnotations.Fast
    public void xor3sparseVector() throws Exception {
        String[] dataset = getDatasetArgs("neural/xor/sparse");

        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = 1000;

        Pair<Pipeline, ?> results = Main.main(dataset, settings);
        ClassificationResults classificationResults = (ClassificationResults) results.s;
        assertEquals(classificationResults.accuracy, 1.0);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: " + timeTaken);
        Duration limit = Duration.ofSeconds(4);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }

    @TestAnnotations.Fast
    public void sparseMatrixCheck() throws Exception {
        NeuralogicLexer lex = new NeuralogicLexer(CharStreams.fromFileName("../Resources/datasets/simple/parsing/test_template")); // transforms characters into tokens
        CommonTokenStream tokens = new CommonTokenStream(lex); // a token stream
        NeuralogicParser parser = new NeuralogicParser(tokens); // transforms tokens into parse trees
        String s = parser.templateFile().templateLine(2).lrnnRule().atom().weight().value().getText();
        assertEquals(s, "[3,4|(1,2:0.3),(2,3:0.1)]");
        //System.out.println(s);
    }
}
