import utils.CommandLineHandler;

import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        CommandLineHandler.main(args);
    }

    public static String testConnection(String msg) {
        return msg + " succesfully connected";
    }

}