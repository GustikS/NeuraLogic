package utils;

import org.apache.commons.cli.*;
import settings.DefaultSettings;

import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class CommandLineHandler {
    private static final Logger LOG = Logger.getLogger(CommandLineHandler.class.getName());

    public CommandLine parseParams(String[] args) throws ParseException {
        Options options = getOptions();
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

    public Options getOptions(DefaultSettings defaults) {
        Options options = new Options();

        options.addRequiredOption("q", "queries", true, "queries file (" + defaults.queriesFile + ")");
        options.addOption("t", "template", true, "template file (" + defaults.templateFile + ")");
        options.addOption("e", "examples", true, "examples file containing facts (" + defaults.examplesFile + ")");

        options.addOption("path", "sourcePath", true, "path to source files (" + defaults.sourcePath + ")");

        // Selection of one of evaluation modes
        OptionGroup evalGroup = new OptionGroup();
        // with test file given
        evalGroup.addOption(new Option("test", "testQueries", true, "file with test queries (" + defaults.testFile + ")"));
        // with crossvalidation folds given
        evalGroup.addOption(Option.builder("folds").optionalArg(true).longOpt("foldPrefix").numberOfArgs(1).desc("folds folder names prefix (" + defaults.foldsPrefix + ")").build());
        // with single file to split given
        evalGroup.addOption(new Option("xval", "crossvalidation", true, "number of folds to split for crossvalidation (" + defaults.folds + ")"));
        options.addOptionGroup(evalGroup);

        options.addOption(new Option("strat", "stratified", true, "stratified crossvalidation (" + defaults.stratification + ")"));

        options.addOption(new Option("seed", "randomSeed", true, "int seed for random generator (" + defaults.seed + ")"));


        //TODO rest of the commandline options

        return options;
    }

    public void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar neuralogic.jar", options);
    }

}
