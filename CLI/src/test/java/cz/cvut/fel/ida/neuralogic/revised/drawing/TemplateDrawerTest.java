package cz.cvut.fel.ida.neuralogic.revised.drawing;

import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.GroundingDebugger;
import cz.cvut.fel.ida.pipelines.debugging.NeuralDebugger;
import cz.cvut.fel.ida.pipelines.debugging.TemplateDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class TemplateDrawerTest {
    private static final Logger LOG = Logger.getLogger(TemplateDrawerTest.class.getName());

    @TestAnnotations.Interactive
    public void simpleGNN() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        settings.drawingDetail = Settings.Detail.LOW;
        Sources sources = Runner.getSources(getDatasetArgs("simple/visual", "-t ./template2.txt"), settings);
        TemplateDebugger templateDebugger = new TemplateDebugger(sources, settings);
        templateDebugger.executeDebug();
    }


    @TestAnnotations.Interactive
    public void drawing() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        Sources sources = Runner.getSources(getDatasetArgs("simple/drawing", "-t ./template.txt"), settings);
        TemplateDebugger templateDebugger = new TemplateDebugger(sources, settings);
        templateDebugger.executeDebug();
    }


}
