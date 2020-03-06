import settings.Settings;
import utils.Runner;

import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        main(args, new Settings());
    }

    public static void main(String[] args, Settings settings) {
        Runner.main(args, settings);
    }

    public static String testConnection(String socket) {
        return socket + " succesfully connected";
    }

}