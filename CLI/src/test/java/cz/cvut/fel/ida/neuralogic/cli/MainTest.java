package cz.cvut.fel.ida.neuralogic.cli;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@RunWith(Parameterized.class)
public class MainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    public static String[] splitArgs(String args) {
        return args.split(" (?=\")|(?<=\")\\s");
    }

    public interface SlowTests {
    }

    public interface FastTests {
    }

    public interface CLITests extends FastTests {
    }

    public interface PerformanceTests extends SlowTests {
    }
//
//    @BeforeEach
//    void setUp() {
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Parameterized.Parameter
//    public String[] args;
//
//    public MainTest(String[] args) {
//        this.args = args;
//    }

    @Test
    public void emptyArgsMsg() {
        Main.main(splitArgs(""));
        assertThat(outContent.toString(), containsString("Lifted Relational Neural Network"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "wrong",
            "",
            "a 2",
            "  "
    })
    void noArgs(String argString) {
        Exception thrown = assertThrows(
                Exception.class,
                () -> Main.mainExc(splitArgs(argString)),
                "Expected Main.mainExc() to throw Exception, but it didn't"
        );

        assertThat(thrown.getMessage(), containsString("No arguments provided"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-a 2",
            "-xxx",
            "-t",
            "-source"
    })
    void wrongArgs(String argString) {
        Exception thrown = assertThrows(
                Exception.class,
                () -> Main.mainExc(splitArgs(argString)),
                "Expected Main.mainExc() to throw Exception, but it didn't"
        );

        assertThat(thrown.getMessage(), containsString("Unable to parse arguments"));
    }
}