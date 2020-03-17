package cz.cvut.fel.ida.neuralogic.cli.dummy;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

public class DummyTests {
    private static final Logger LOG = Logger.getLogger(DummyTests.class.getName());

    @TestAnnotations.Fast
    void dummyTest(){
        System.out.println("dummy");
    }

}
