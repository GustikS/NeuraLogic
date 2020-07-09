package cz.cvut.fel.ida.neuralogic.cli.utils;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class ResourceFilesTest {
    private static final Logger LOG = Logger.getLogger(ResourceFilesTest.class.getName());

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

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

    @TestAnnotations.Fast
    public void listAvailableResources() {
        boolean available = false;
        Enumeration<URL> e = null;
        try {
            e = getClass().getClassLoader().getResources(".");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while (e.hasMoreElements()) {
            URL u = e.nextElement();
            String file = u.getFile();
            System.out.println(file);
            if (file.endsWith("/NeuraLogic/Resources/target/classes/"))
                available = true;
        }
        assert available;
    }

    @TestAnnotations.Fast
    void checkDatasetsAvailable() throws IOException {
        String path = Utilities.getResourcePath("simple");
        List<Path> collect = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .peek(System.out::println)
                .filter(s -> s.endsWith("family/template.txt"))
                .collect(Collectors.toList());
        assert !collect.isEmpty();
    }
}