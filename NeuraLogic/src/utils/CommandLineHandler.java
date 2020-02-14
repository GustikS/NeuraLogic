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

        //-----------source files
        options.addOption("sf", "sources", true, "path to json sources specification (" + settings.sourcesFile + ")");
        options.addOption("path", "sourcePath", true, "path to source files (" + settings.sourcePath + ")");

        options.addOption("t", "template", true, "template file (" + settings.templateFile + ")");

        options.addOption("q", "trainQueries", true, "trainQueries file (" + settings.trainQueriesFile + ")");
        options.addOption("e", "trainExamples", true, "trainExamples file containing facts (" + settings.trainExamplesFile + ")");

        //-----------settings

        options.addOption(new Option("set", "settings", true, "json file with all the settings (" + settings.settingsFile + ")"));

        options.addOption(new Option("out", "outputFolder", true, "output folder for logging and exporting (" + settings.outDir + ")"));

        options.addOption(new Option("mode", "pipelineMode", true, "main mode of the program [complete, neuralization, debug] (" + settings.mainMode + ")"));

        options.addOption("debug", "debugMode", true, "debug some objects within the pipeline during the run [template, grounding, neuralization, samples, model]");

        options.addOption("lim", "limitExamples", true, "limit examples to some smaller number (use e.g. for debugging) (" + settings.appLimitSamples + ")");

        options.addOption(new Option("seed", "randomSeed", true, "int seed for random generator (" + settings.seed + ")"));

        //-----------selection of one of evaluation modes
        OptionGroup evalGroup = new OptionGroup();
        // with test files given
        evalGroup.addOption(new Option("tq", "testQueries", true, "file with test queries (" + settings.testQueriesFile + ")"));
        evalGroup.addOption(new Option("te", "testExamples", true, "file with test examples (" + settings.testExamplesFile + ")"));
        // with crossvalidation folds given
        evalGroup.addOption(Option.builder("folds").optionalArg(true).longOpt("foldPrefix").numberOfArgs(1).desc("folds folder names prefix (" + settings.foldsPrefix + ")").build());
        // with single file to xval split given
        evalGroup.addOption(new Option("xval", "crossvalidation", true, "number of folds to split for crossvalidation (" + settings.foldsCount + ")"));
        options.addOptionGroup(evalGroup);

        //grounding
        options.addOption(new Option("gm", "groundingMode", true, "groundings mode [normal, sequential, global] (" + "normal" + ")"));
        options.addOption(new Option("ga", "groundingAlgorithm", true, "groundings algorithm [BUp, TDown, Gringo] (" + "BUp" + ")"));

        //training
        options.addOption(new Option("init", "weightInit", true, "distribbution for weight initialization [uniform, longtail, constant] (" + settings.initDistribution + ")"));
        options.addOption(new Option("opt", "optimizer", true, "optimization algorithm (" + settings.optimizer + ")"));
        options.addOption(new Option("lr", "learningRate", true, "initial learning rate (" + settings.initLearningRate + ")"));
        options.addOption(new Option("ts", "trainingSteps", true, "cumulative number of epochae in neural training (" + settings.maxCumEpochCount + ")"));

        //evaluation
        options.addOption(new Option("em", "evaluationMode", true, "evaluation is either [regression, classification] (" + "classification" + ")"));
        options.addOption(new Option("ef", "errorFunction", true, "type of error function [MSE, XEnt] (" + "MSE" + ")"));

        //compression
        options.addOption(new Option("iso", "isoCompression", true, "iso-value network compression (lifting), number of decimal digits (" + settings.isoDecimals + ")"));
        options.addOption(new Option("isoinits", "isoInitializations", true, "number of iso-value initializations for network compression (lifting) (" + settings.isoValueInits + ")"));
        options.addOption(new Option("isocheck", "losslessCompression", true, "lossless compression isomorphism extra check? (" + settings.losslessIsoCompression + ")"));
        options.addOption(new Option("prune", "chainPruning", true, "linear chain network pruning (" + (settings.chainPruning ? 1 : 0) + ")"));

        //todo rest of the commandline options that might be useful

        return options;
    }

    public void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar neuralogic.jar", options);
    }

}
