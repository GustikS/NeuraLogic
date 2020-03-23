package cz.cvut.fel.ida.old_tests.utils;

import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import org.junit.jupiter.api.Test;

public class CommandLineHandlerTest {


    @Test
    public void empty() throws Exception {
        String[] args = "".split(" ");
        Runner.main(args);
    }

    @Test
    public void parsing_complex() throws Exception {
        String[] args = "-t ./resources/datasets/simple/parsing/test_template -q ./resources/datasets/simple/parsing/queries".split(" ");
        Runner.main(args);
    }


    @Test
    public void missing_template1() throws Exception {
        String[] args = "-q ./resources/datasets/simple/parsing/queries".split(" ");
        Runner.main(args);
    }

    @Test
    public void missing_template2() throws Exception {
        String[] args = "-path ./resources/datasets/simple/family".split(" ");
        Runner.main(args);
    }
}