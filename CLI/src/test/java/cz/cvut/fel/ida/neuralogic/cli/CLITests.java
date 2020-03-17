package cz.cvut.fel.ida.neuralogic.cli;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static cz.cvut.fel.ida.utils.generic.Utilities.splitArgs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@RunWith(Parameterized.class)
@Tag("main")
@Tag("fast")
@DisplayName("Testing the basic commandline inputs")
public class CLITests {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Nested
    @Tag("cli")
    public class CommandLine {

        @Test
        @DisplayName("Test empty commandline help message printing")
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
        void wrongInput(String argString) {
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
}