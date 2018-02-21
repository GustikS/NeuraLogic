import utils.CommandLine;
import ida.utils.tuples.Pair;
import pipelines.Pipeline;
import settings.Settings;
import utils.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Created by gusta on 8.3.17.
 */
public class Main {

    public static void main(String[] args) {
        Logger.initializeLogging();
        CommandLine cmd = new CommandLine();
        Map<String, String> params = cmd.parseParams(args);
        Settings settings = null;
        try {
            settings = Settings.constructFrom(params);
        } catch (IOException ex) {
            Logger.err("Couldn't construct initial settings due to IOException: " + ex.getMessage());
            System.exit(1);
        }
        //place for external changes in setting object for non-standard pipelines
        Pair<Boolean, String> validation = settings.validate();
        if (!validation.r) {
            Logger.err("Invalid pipeline setting, unable to run due to: " + validation.s);
            System.exit(2);
        }
        Pipeline pipeline = Pipeline.buildFrom(settings);
        pipeline.execute(settings);
    }
}
