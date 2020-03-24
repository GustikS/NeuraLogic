package cz.cvut.fel.ida.pipelines.debugging;

import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.SourceFiles;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

class GroundingDebuggerTest {

    @TestAnnotations.Slow
    public void family() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.intermediateDebug = false;
        SourceFiles sourceFiles = SourceFiles.loadFromJson(settings, "simple/family");

        GroundingDebugger groundingDebugger = new GroundingDebugger(sourceFiles, settings);
        groundingDebugger.executeDebug();
    }
}