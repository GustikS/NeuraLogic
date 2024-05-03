package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.learning.results.ClassificationResults;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.debugging.GroundingDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Templates {
    private static final Logger LOG = Logger.getLogger(Templates.class.getName());

    @TestAnnotations.Slow
    public void mutaGraphlets() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.resultsRecalculationEpochae = 10;
        settings.maxCumEpochCount = 100;
        Main.main(getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_graphlets.txt"), settings);
    }

    @TestAnnotations.Interactive
    public void typingDebug() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        Sources sources = Runner.getSources(getDatasetArgs("debug/typing"), settings);
        GroundingDebugger groundingDebugger = new GroundingDebugger(sources, settings);
        groundingDebugger.executeDebug();
    }

    @TestAnnotations.Fast
    public void typingRun() throws Exception {
        Settings settings = Settings.forFastTest();
        String[] dataset = getDatasetArgs("debug/typing");
        Main.main(dataset, settings);
    }
}