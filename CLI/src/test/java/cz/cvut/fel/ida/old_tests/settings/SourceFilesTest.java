package cz.cvut.fel.ida.old_tests.settings;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.TrainingDebugger;
import cz.cvut.fel.ida.setup.Settings;
import org.junit.Test;

public class SourceFilesTest {

    @Test
    public void multipleTemplates() throws Exception {
        Logging logging = Logging.initLogging();
        Settings settings = new Settings();

        String[] args = ("-path ./resources/datasets/relational/molecules/muta_mini " +
                " -t ./embeddings,./template.txt" +
                " -out ./out/multitempl").split(" ");

        TrainingDebugger trainingDebugger = new TrainingDebugger(Runner.getSources(args, settings), settings);
        trainingDebugger.executeDebug();
    }

}