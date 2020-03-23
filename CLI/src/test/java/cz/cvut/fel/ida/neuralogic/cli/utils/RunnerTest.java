package cz.cvut.fel.ida.neuralogic.cli.utils;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;

class RunnerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


//    @Test
    @TestAnnotations.Fast
    void checkResourceFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("relational").getFile());
        System.out.println(file);
    }

    @TestAnnotations.Fast
    void getResource(){
        String tmp2 = Utilities.readResourceFile("dummy.txt");
        System.out.println(tmp2);
    }
}