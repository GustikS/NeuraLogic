package utils;

import org.apache.commons.cli.*;
import settings.Settings;

import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class CommandLineHandler {
    private static final Logger LOG = Logger.getLogger(CommandLineHandler.class.getName());

    public CommandLine parseParams(String[] args, Settings settings) throws ParseException {
        Options options = getOptions(settings);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException ex) {
            printHelp(options);
            throw ex;
        }
        return cmd;
    }

    public Options getOptions(Settings settings) {
        Options options = new Options();

        options.addRequiredOption("q", "trainQueries", true, "trainQueries file (" + settings.trainQueriesFile + ")");
        options.addOption("t", "template", true, "template file (" + settings.templateFile + ")");
        options.addOption("e", "trainExamples", true, "trainExamples file containing facts (" + settings.trainExamplesFile + ")");

        options.addOption("path", "sourcePath", true, "path to source files (" + settings.sourcePath + ")");

        // Selection of one of evaluation modes
        OptionGroup evalGroup = new OptionGroup();
        // with test file given
        evalGroup.addOption(new Option("test", "testQueries", true, "file with test trainQueries (" + settings.testQueriesFile + ")"));
        // with crossvalidation folds given
        evalGroup.addOption(Option.builder("folds").optionalArg(true).longOpt("foldPrefix").numberOfArgs(1).desc("folds folder names prefix (" + settings.foldsPrefix + ")").build());
        // with single file to split given
        evalGroup.addOption(new Option("xval", "crossvalidation", true, "number of folds to split for crossvalidation (" + settings.foldsCount + ")"));
        options.addOptionGroup(evalGroup);

        options.addOption(new Option("strat", "stratified", true, "stratified crossvalidation (" + settings.stratification + ")"));

        options.addOption(new Option("seed", "randomSeed", true, "int seed for random generator (" + settings.seed + ")"));


        //TODO rest of the commandline options

        return options;
    }

    public void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar neuralogic.jar", options);
    }

}
