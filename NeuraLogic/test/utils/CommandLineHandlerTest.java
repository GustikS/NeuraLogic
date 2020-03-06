package utils;

import org.junit.Test;

public class CommandLineHandlerTest {


    @Test
    public void empty(){
        String[] args = "".split(" ");
        Runner.main(args);
    }

    @Test
    public void parsing_complex() {
        String[] args = "-t ./resources/datasets/simple/parsing/test_template -q ./resources/datasets/simple/parsing/queries".split(" ");
        Runner.main(args);
    }


    @Test
    public void missing_template1(){
        String[] args = "-q ./resources/datasets/simple/parsing/queries".split(" ");
        Runner.main(args);
    }

    @Test
    public void missing_template2(){
        String[] args = "-path ./resources/datasets/simple/family".split(" ");
        Runner.main(args);
    }
}