package networks.computation.training.debugging;

import org.junit.Test;
import settings.Settings;

public class TrainingDebuggerTest {

    @Test
    public void xor() {
        String[] args = new String("-path ./resources/datasets/neural/xor/vectorized/").split(" ");
        Settings settings = new Settings();
        TrainingDebugger trainingDebugger = new TrainingDebugger(args, settings);
    }

}