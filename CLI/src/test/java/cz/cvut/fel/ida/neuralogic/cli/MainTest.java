package cz.cvut.fel.ida.neuralogic.cli;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static cz.cvut.fel.ida.utils.generic.Utilities.splitArgs;

public class MainTest {
    private static final Logger LOG = Logger.getLogger(MainTest.class.getName());

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

    @TestAnnotations.Slow
    public void runHorseFamily() {
        String resourcePath = Utilities.getResourcePath("simple/family");
        try {
            Main.main(splitArgs("-ts 10 -sd " + resourcePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
