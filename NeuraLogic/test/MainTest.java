import org.junit.Test;

public class MainTest {

    @Test
    public void main() {
        String[] args = new String("-e ./resources/datasets/family/examples -t ./resources/datasets/family/template -q ./resources/datasets/family/queries").split(" ");
        Main.main(args);
    }
}
