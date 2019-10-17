import org.junit.Test;
import settings.Settings;

import java.util.logging.Level;

public class MainTest {

    @Test
    public void parsing() {
        String[] args = new String("-t ./resources/parsing/test_template -q ./resources/parsing/queries").split(" ");
        Main.main(args);
    }

    @Test
    public void family() {
        String[] args = new String("-e ./resources/datasets/family/examples -t ./resources/datasets/family/template -q ./resources/datasets/family/queries").split(" ");
        Settings settings = new Settings();
        settings.shuffleBeforeTraining = false;
        settings.initLearningRate = 1;
        settings.maxCumEpochCount = 1000;
        Settings.loggingLevel = Level.FINEST;
        Main.main(args,settings);
    }

    @Test
    public void mutagenesis() {
        String[] args = new String("-e ./resources/datasets/mutagenesis/examples -t ./resources/datasets/mutagenesis/template_old -q ./resources/datasets/mutagenesis/queries").split(" ");

        Settings settings = new Settings();
        Settings.loggingLevel = Level.FINER;
        //settings.limitSamples = 10;
        settings.maxCumEpochCount = 10000;
        //settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = true;
        settings.pruneNetworks = false;
        settings.isoValueCompression = true;

        Main.main(args, settings);
    }

    @Test
    public void mutamini() {
        String[] args = new String("-e ./resources/datasets/muta_mini/examples -t ./resources/datasets/muta_mini/template_old").split(" ");

        Settings settings = new Settings();
        Settings.loggingLevel = Level.FINER;
        settings.initLearningRate = 0.1;
        settings.maxCumEpochCount = 20;
        settings.shuffleBeforeTraining = false;
        //settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = true;
        settings.pruneNetworks = true;

        Main.main(args, settings);
    }

    @Test
    public void mutanokappa() {
        String[] args = new String("-e ./resources/datasets/mutagenesis/examples -t ./resources/datasets/mutagenesis/template_new -q ./resources/datasets/mutagenesis/queries").split(" ");

        Settings settings = new Settings();
        Settings.loggingLevel = Level.FINER;
        settings.initLearningRate = 0.1;
        settings.maxCumEpochCount = 20;
        settings.shuffleBeforeTraining = false;
        //settings.oneQueryPerExample = true;

        Main.main(args, settings);
    }

    @Test
    public void p388() {
        String[] args = new String("-e ./resources/datasets/p388/examples -t ./resources/datasets/p388/normalFully").split(" ");

        Settings settings = new Settings();
        Settings.loggingLevel = Level.FINER;
        settings.initLearningRate = 0.1;
        settings.maxCumEpochCount = 2000;
        settings.shuffleBeforeTraining = false;
        //settings.oneQueryPerExample = true;

        Main.main(args, settings);
    }
}
