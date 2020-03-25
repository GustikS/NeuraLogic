package cz.cvut.fel.ida.neuralogic.cli.utils;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.logging.Logger;

class ResourceFilesTest {
    private static final Logger LOG = Logger.getLogger(ResourceFilesTest.class.getName());

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
        LOG.fine(file.toString());
    }

    @TestAnnotations.Fast
    void getResource() {
        String tmp2 = Utilities.readResourceFile("dummy.txt");
        LOG.fine(tmp2);
    }
}