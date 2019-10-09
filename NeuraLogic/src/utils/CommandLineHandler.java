package utils;

import org.apache.commons.cli.*;
import settings.Settings;

import java.util.Arrays;
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
        LOG.info("with arguments: " + Arrays.toString(args) + " ...parsed into:");
        try {
            cmd = parser.parse(options, args);
            for (Option option : cmd.getOptions()) {
                LOG.info(option.getLongOpt() + " : " + option.getValue());
            }
        } catch (Exception ex) {
            LOG.severe("Unable to parse arguments!");
            printHelp(options);
            throw ex;
        }
        return cmd;
    }

    public Options getOptions(Settings settings) {
        Options options = new Options();

        options.addOption(new Option("seed", "randomSeed", true, "int seed for random generator (" + settings.seed + ")"));

        //-----------source files
        options.addOption("t", "template", true, "template file (" + settings.templateFile + ")");

        options.addOption("q", "trainQueries", true, "trainQueries file (" + settings.trainQueriesFile + ")");
        options.addOption("e", "trainExamples", true, "trainExamples file containing facts (" + settings.trainExamplesFile + ")");

        options.addOption("path", "sourcePath", true, "path to source files (" + settings.sourcePath + ")");

        //-----------selection of one of evaluation modes
        OptionGroup evalGroup = new OptionGroup();
        // with test file given
        evalGroup.addOption(new Option("test", "testQueries", true, "file with test trainQueries (" + settings.testQueriesFile + ")"));
        // with crossvalidation folds given
        evalGroup.addOption(Option.builder("folds").optionalArg(true).longOpt("foldPrefix").numberOfArgs(1).desc("folds folder names prefix (" + settings.foldsPrefix + ")").build());
        // with single file to xval split given
        evalGroup.addOption(new Option("xval", "crossvalidation", true, "number of folds to split for crossvalidation (" + settings.foldsCount + ")"));
        options.addOptionGroup(evalGroup);

        //grounding
        options.addOption(new Option("gm", "groundingMode", true, "groundings mode - normal, sequential, or global (" + "normal" + ")"));
        options.addOption(new Option("ga", "groundingAlgorithm", true, "groundings algorithm - BUp, TDown, or Gringo (" + "BUp" + ")"));

        //training
        options.addOption(new Option("ts", "trainingSteps", true, "cumulative number of epochae in neural training (" + settings.maxCumEpochCount + ")"));

        //evaluation
        options.addOption(new Option("em", "evaluationMode", true, "evaluation is either regression or classification (" + "classification" + ")"));
        options.addOption(new Option("ef", "errorFunction", true, "type of error function - MSE, or XEnt (" + "MSE" + ")"));

        //todo rest of the commandline options that might be useful

        return options;
    }

    public void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar neuralogic.jar", options);
    }

}
