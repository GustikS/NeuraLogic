package cz.cvut.fel.ida.neuralogic.revised.drawing;

import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.GroundingDebugger;
import cz.cvut.fel.ida.pipelines.debugging.NeuralDebugger;
import cz.cvut.fel.ida.pipelines.debugging.TemplateDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class GroundingDrawerTest {

    @TestAnnotations.Interactive
    public void family() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);
        GroundingDebugger groundingDebugger = new GroundingDebugger(sources, settings);
        groundingDebugger.executeDebug();
    }


    @TestAnnotations.Interactive
    public void noquery() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        settings.intermediateDebug = true;
        settings.debugTemplate = true;
//        settings.compactGroundingDrawing = true;
        Sources sources = Runner.getSources(getDatasetArgs("debug/drawing/noquery"), settings);
        GroundingDebugger groundingDebugger = new GroundingDebugger(sources, settings);
        groundingDebugger.executeDebug();
    }

    @TestAnnotations.Interactive
    public void ecoli() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        Sources sources = Runner.getSources(getDatasetArgs("relational/molecules/e_coli/mini"), settings);
        GroundingDebugger groundingDebugger = new GroundingDebugger(sources, settings);
        groundingDebugger.executeDebug();
    }
}