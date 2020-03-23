package cz.cvut.fel.ida.old_tests.utils;

import org.junit.Test;
import sun.misc.Launcher;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by gusta on 27.2.18.
 */
public class UtilsTests {

    @Test
    public void systemInfoTest() {
        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " +
                Runtime.getRuntime().availableProcessors());

         /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " +
                Runtime.getRuntime().freeMemory());

         /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
         /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

         /* Total memory currently in use by the JVM */
        System.out.println("Total memory (bytes): " +
                Runtime.getRuntime().totalMemory());

          /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();

         /* For each filesystem root, print some info */
        for (File root : roots) {
            System.out.println("File system root: " + root.getAbsolutePath());
            System.out.println("Total space (bytes): " + root.getTotalSpace());
            System.out.println("Free space (bytes): " + root.getFreeSpace());
            System.out.println("Usable space (bytes): " + root.getUsableSpace());
        }
    }

    @Test
    public void jartest(){
        String path = ".";
        final URL url = Launcher.class.getResource("/" + path);
        if (url != null) {
            try {
                final File apps = new File(url.toURI());
                for (File app : apps.listFiles()) {
                    System.out.println(app);
                }
            } catch (URISyntaxException ex) {
                // never happens
            }
        }
    }
}
