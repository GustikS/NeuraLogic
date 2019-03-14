import org.junit.Test;

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
}
