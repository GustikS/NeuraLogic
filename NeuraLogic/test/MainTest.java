import org.junit.Test;
import settings.Settings;

public class MainTest {

    @Test
    public void family() {
        String[] args = new String("-e ./resources/datasets/family/examples -t ./resources/datasets/family/template -q ./resources/datasets/family/queries").split(" ");
        Main.main(args);
    }

    @Test
    public void parsing() {
        String[] args = new String("-t ./resources/parsing/test_template -q ./resources/parsing/queries").split(" ");
        Main.main(args);
    }

    @Test
    public void mutagenesis() {
        String[] args = new String("-e ./resources/datasets/mutagenesis/examples -t ./resources/datasets/mutagenesis/template_old -q ./resources/datasets/mutagenesis/queries").split(" ");

        Settings settings = new Settings();
        settings.limitSamples = 100;
        settings.maxCumEpochCount = 0;

        Main.main(args, settings);
    }
}
