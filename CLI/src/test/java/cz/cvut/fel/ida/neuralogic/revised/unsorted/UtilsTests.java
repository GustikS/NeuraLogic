package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import sun.misc.Launcher;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by gusta on 27.2.18.
 */
public class UtilsTests {

    private static final Logger LOG = Logger.getLogger(UtilsTests.class.getName());
    @TestAnnotations.Fast
    public void systemInfoTest() {
        /* Total number of processors or cores available to the JVM */
        LOG.fine("Available processors (cores): " +
                Runtime.getRuntime().availableProcessors());

         /* Total amount of free memory available to the JVM */
        LOG.fine("Free memory (bytes): " +
                Runtime.getRuntime().freeMemory());

         /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
         /* Maximum amount of memory the JVM will attempt to use */
        LOG.fine("Maximum memory (bytes): " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

         /* Total memory currently in use by the JVM */
        LOG.fine("Total memory (bytes): " +
                Runtime.getRuntime().totalMemory());

          /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();

         /* For each filesystem root, print some info */
        for (File root : roots) {
            LOG.fine("File system root: " + root.getAbsolutePath());
            LOG.fine("Total space (bytes): " + root.getTotalSpace());
            LOG.fine("Free space (bytes): " + root.getFreeSpace());
            LOG.fine("Usable space (bytes): " + root.getUsableSpace());
        }
    }

    @TestAnnotations.Fast
    public void jartest(){
        String path = ".";
        final URL url = Launcher.class.getResource("/" + path);
        if (url != null) {
            try {
                final File apps = new File(url.toURI());
                for (File app : apps.listFiles()) {
                    LOG.fine(app.toString());
                }
            } catch (URISyntaxException ex) {
                //never happens
                fail();
            }
        }
    }
}
