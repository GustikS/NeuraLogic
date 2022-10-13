package cz.cvut.fel.ida.neuralogic.cli.utils;

import cz.cvut.fel.ida.setup.Settings;
import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class CommandLineHandler {
    private static final Logger LOG = Logger.getLogger(CommandLineHandler.class.getName());

    public CommandLine parseParams(String[] args, Settings settings) throws Exception {
        Options options = getOptions(settings);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        LOG.info("with arguments: " + Arrays.toString(args) + " ...parsed into:\n");
        try {
            cmd = parser.parse(options, args);
            if (cmd.getOptions().length == 0) {
                printHelp(options);
                throw new Exception("No arguments provided. You need to specify input data/model for learning.");
            }
            for (Option option : cmd.getOptions()) {
                LOG.info(option.getLongOpt() + " : " + option.getValue());
            }
        } catch (ParseException ex) {
            printHelp(options);
            throw new Exception("Unable to parse arguments!");
        }
        return cmd;
    }

    public Options getOptions(Settings settings) {
        Options options = new Options();


        options.addOption(Option.builder("lc").longOpt("logColors").argName("INT").numberOfArgs(1).optionalArg(true).desc("colored output on console, best on white background {0,INT} (default: " + (settings.customLogColors ? 1 : 0) + ")").build());

        //-----------source files
        options.addOption(Option.builder("sf").longOpt("sourcesFile").argName("FILE").numberOfArgs(1).optionalArg(true).desc("path to json Sources specification file (default: " + settings.sourcesFile + ")").build());
        options.addOption(Option.builder("sd").longOpt("sourcesDir").argName("DIR").numberOfArgs(1).optionalArg(true).desc("path to directory with all the standardly-named Source files for learning (default: " + settings.sourcePath + ")").build());
        options.addOption(Option.builder("t").longOpt("template").argName("FILE").numberOfArgs(1).optionalArg(true).desc("Template file containing weighted rules for leaning (default: " + settings.templateFile + ")").build());
        options.addOption(Option.builder("q").longOpt("trainQueries").argName("FILE").numberOfArgs(1).optionalArg(true).desc("file containing labeled training Queries (default: " + settings.trainQueriesFile + ")").build());
        options.addOption(Option.builder("e").longOpt("trainExamples").argName("FILE").numberOfArgs(1).optionalArg(true).desc("file containing, possibly labeled, input training Facts (default: " + settings.trainExamplesFile + ")").build());
        options.addOption(Option.builder("vq").longOpt("valQueries").argName("FILE").numberOfArgs(1).optionalArg(true).desc("file containing labeled validation Queries (default: " + settings.valQueriesFile + ")").build());
        options.addOption(Option.builder("ve").longOpt("valExamples").argName("FILE").numberOfArgs(1).optionalArg(true).desc("file containing, possibly labeled, input validation Facts (default: " + settings.valExamplesFile + ")").build());

        //-----------selection of one of evaluation modes
        OptionGroup evalGroup = new OptionGroup();
        // with test files given
        evalGroup.addOption(Option.builder("tq").longOpt("testQueries").argName("FILE").numberOfArgs(1).optionalArg(true).desc("file with test Queries (default: " + settings.testQueriesFile + ")").build());
        options.addOption(Option.builder("te").longOpt("testExamples").argName("FILE").numberOfArgs(1).optionalArg(true).desc("file with, possibly labeled, test Examples (default: " + settings.testExamplesFile + ")").build());
        // with crossvalidation folds given
        evalGroup.addOption(Option.builder("fp").argName("STRING").optionalArg(true).longOpt("foldPrefix").numberOfArgs(1).desc("folds folder names prefix (default: " + settings.foldsPrefix + ")").build());
        // with single file to xval split given
        evalGroup.addOption(Option.builder("xval").longOpt("crossvalidation").argName("INT").numberOfArgs(1).optionalArg(true).desc("number of folds to split for crossvalidation (default: " + settings.foldsCount + ")").build());
        options.addOptionGroup(evalGroup);

        //-----------settings
        options.addOption(Option.builder("set").longOpt("settingsFile").argName("FILE").numberOfArgs(1).optionalArg(true).desc("path to json file with all the Settings (default: " + settings.settingsFile + ")").build());
        options.addOption(Option.builder("out").longOpt("outputFolder").argName("DIR").numberOfArgs(1).optionalArg(true).desc("output folder for logging and exporting (default: " + settings.outDir + ")").build());
        options.addOption(Option.builder("mode").longOpt("pipelineMode").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("main mode of the program {complete, neuralization, debug} (default: " + settings.mainMode.toString().toLowerCase() + ")").build());
        options.addOption(Option.builder("debug").longOpt("debugMode").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("debug some objects within the Pipeline during the run {template, grounding, neuralization, samples, model, all} (default: all)").build());
        options.addOption(Option.builder("lim").longOpt("limitExamples").argName("INT").numberOfArgs(1).optionalArg(true).desc("limit examples to some smaller number, used e.g. for debugging {-1,INT} (default: " + settings.appLimitSamples + ")").build());
        options.addOption(Option.builder("seed").longOpt("randomSeed").argName("INT").numberOfArgs(1).optionalArg(true).desc("int seed for random generator (default: " + settings.seed + ")").build());

        //grounding
        options.addOption(Option.builder("gm").longOpt("groundingMode").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("groundings mode {independent, sequential, global} (default: " + settings.groundingMode.name().toLowerCase() + ")").build());

        //training
        options.addOption(Option.builder("dist").longOpt("distribution").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("distribution for weight initialization {uniform, normal, longtail, constant} (default: " + settings.initDistribution.toString().toLowerCase() + ")").build());
        options.addOption(Option.builder("init").longOpt("initialization").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("algorithm for weight initialization {simple, glorot, he} (default: " + settings.initializer.toString().toLowerCase() + ")").build());
        options.addOption(Option.builder("opt").longOpt("optimizer").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("optimization algorithm {sgd, adam} (default: " + settings.getOptimizer() + ")").build());
        options.addOption(Option.builder("lr").longOpt("learningRate").argName("FLOAT").numberOfArgs(1).optionalArg(true).desc("initial learning rate (default: " + settings.initLearningRate + ")").build());
        options.addOption(Option.builder("ts").longOpt("trainingSteps").argName("INT").numberOfArgs(1).optionalArg(true).desc("cumulative number of epochae in neural training (default: " + settings.maxCumEpochCount + ")").build());
        options.addOption(Option.builder("rec").longOpt("recalculationEpocha").argName("INT").numberOfArgs(1).optionalArg(true).desc("recalculate true training and validation error+stats every {INT} epochae (default: " + settings.resultsRecalculationEpochae + ")").build());
        options.addOption(Option.builder("decay").longOpt("learnRateDecay").argName("FLOAT").numberOfArgs(1).optionalArg(true).desc("learning rate decay geometric coefficient {-1,FLOAT} (default: " + settings.learnRateDecay + ")").build());
        options.addOption(Option.builder("decays").longOpt("decaySteps").argName("INT").numberOfArgs(1).optionalArg(true).desc("learning rate decays every {-1,INT} steps (default: " + settings.decaySteps + ")").build());

        //functions
        options.addOption(Option.builder("atomf").longOpt("atomFunction").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("activation function for atom neurons {sigmoid,tanh,relu,identity,...} (default: " + settings.atomNeuronTransformation.name().toLowerCase() + ")").build());
        options.addOption(Option.builder("rulef").longOpt("ruleFunction").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("activation function for rule neurons {sigmoid,tanh,relu,identity,...} (default: " + settings.ruleNeuronTransformation.name().toLowerCase() + ")").build());
        options.addOption(Option.builder("aggf").longOpt("aggFunction").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("aggregation function for aggregation neurons {avg,max,sum,...} (default: " + settings.aggNeuronAggregation.name().toLowerCase() + ")").build());

        //evaluation
        options.addOption(Option.builder("em").longOpt("evaluationMode").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("evaluation metrics are either for {regression, classification, kbc} (default: " + "classification" + ")").build());
        options.addOption(Option.builder("ef").longOpt("errorFunction").argName("ENUM").numberOfArgs(1).optionalArg(true).desc("type of error function {MSE, XEnt} (default: " + settings.errorAggregationFcn.name() + "(" + settings.errorFunction.name() + "))").build());

        //compression
        options.addOption(Option.builder("iso").longOpt("isoCompression").argName("INT").numberOfArgs(1).optionalArg(true).desc("iso-value network compression (lifting), number of decimal digits (default: " + settings.isoDecimals + ")").build());
        options.addOption(Option.builder("isoinits").longOpt("isoInitializations").argName("INT").numberOfArgs(1).optionalArg(true).desc("number of iso-value initializations for network compression (default: " + settings.isoValueInits + ")").build());
        options.addOption(Option.builder("isocheck").longOpt("losslessCompression").argName("INT").numberOfArgs(1).optionalArg(true).desc("lossless compression isomorphism extra check? {0,1} (default: " + (settings.structuralIsoCompression ? 1 : 0) + ")").build());
        options.addOption(Option.builder("prune").longOpt("chainPruning").argName("INT").numberOfArgs(1).optionalArg(true).desc("linear chain network pruning {0,1} (default: " + (settings.chainPruning ? 1 : 0) + ")").build());

        return options;
    }

    @Deprecated
    public Options getOptionsOld(Settings settings) {
        Options options = new Options();

        options.addOption("lc", "logColors", true, "colored output on console [0,1] (default: " + settings.customLogColors + ")");

        //-----------source files
        options.addOption("sf", "sourcesFile", true, "path to json sources specification file [] (" + settings.sourcesFile + ")");
        options.addOption("sd", "sourcesDir", true, "path to directory with all the standardly-named source files for learning (" + settings.sourcePath + ")");

        options.addOption("t", "template", true, "template file containing weighted rules for leaning (" + settings.templateFile + ")");

        options.addOption("q", "trainQueries", true, "trainQueries file containing labeled queries (" + settings.trainQueriesFile + ")");
        options.addOption("e", "trainExamples", true, "trainExamples file containing, possibly labeled, input facts (" + settings.trainExamplesFile + ")");

        options.addOption("vq", "valQueries", true, "valQueries file containing labeled queries (" + settings.valQueriesFile + ")");
        options.addOption("ve", "valExamples", true, "valExamples file containing, possibly labeled, input facts (" + settings.valExamplesFile + ")");

        //-----------settings

        options.addOption("set", "settingsFile", true, "path to json file with all the settings (" + settings.settingsFile + ")");

        options.addOption("out", "outputFolder", true, "output folder for logging and exporting (" + settings.outDir + ")");

        options.addOption("mode", "pipelineMode", true, "main mode of the program [complete, neuralization, debug] (" + settings.mainMode.toString().toLowerCase() + ")");

        options.addOption("debug", "debugMode", true, "debug some objects within the pipeline during the run [template, grounding, neuralization, samples, model, all] (all)");

        options.addOption("lim", "limitExamples", true, "limit examples to some smaller number (use e.g. for debugging) (" + settings.appLimitSamples + ")");

        options.addOption("seed", "randomSeed", true, "int seed for random generator (" + settings.seed + ")");

        //-----------selection of one of evaluation modes
        OptionGroup evalGroup = new OptionGroup();
        // with test files given
        evalGroup.addOption(new Option("tq", "testQueries", true, "file with test queries (" + settings.testQueriesFile + ")"));
        options.addOption("te", "testExamples", true, "file with test examples (" + settings.testExamplesFile + ")");
        // with crossvalidation folds given
        evalGroup.addOption(Option.builder("fp").optionalArg(true).longOpt("foldPrefix").numberOfArgs(1).desc("folds folder names prefix (" + settings.foldsPrefix + ")").build());
        // with single file to xval split given
        evalGroup.addOption(new Option("xval", "crossvalidation", true, "number of folds to split for crossvalidation (" + settings.foldsCount + ")"));
        options.addOptionGroup(evalGroup);

        //grounding
        options.addOption(new Option("gm", "groundingMode", true, "groundings mode [normal, sequential, global] (" + settings.groundingMode.name() + ")"));
        options.addOption(new Option("ga", "groundingAlgorithm", true, "groundings algorithm [BUp, TDown, Gringo] (" + settings.grounding.name() + ")"));

        //training
        options.addOption(new Option("dist", "distribution", true, "distribution for weight initialization [uniform, normal, longtail, constant] (" + settings.initDistribution.toString().toLowerCase() + ")"));
        options.addOption(new Option("init", "initialization", true, "algorithm for weight initialization [simple, glorot, he] (" + settings.initializer.toString().toLowerCase() + ")"));

        options.addOption(new Option("opt", "optimizer", true, "optimization algorithm (" + settings.getOptimizer() + ")"));
        options.addOption(new Option("lr", "learningRate", true, "initial learning rate (" + settings.initLearningRate + ")"));
        options.addOption(new Option("ts", "trainingSteps", true, "cumulative number of epochae in neural training (" + settings.maxCumEpochCount + ")"));
        options.addOption(new Option("rec", "recalculationEpocha", true, "recalculate true training and validation error+stats every N epochae (" + settings.resultsRecalculationEpochae + ")"));

        options.addOption(new Option("decay", "learnRateDecay", true, "learning rate decay geometric coefficient (-1=off) (" + settings.learnRateDecay + ")"));
        options.addOption(new Option("decays", "decaySteps", true, "learning rate decays every N steps (" + settings.decaySteps + ")"));

        //functions
        options.addOption(new Option("atomagg", "atomCombination", true, "combination function for atom neurons (" + settings.atomNeuronCombination.name().toLowerCase() + ")"));
        options.addOption(new Option("atomf", "atomTransformation", true, "transformation function for atom neurons (" + settings.atomNeuronTransformation.name().toLowerCase() + ")"));
        options.addOption(new Option("ruleagg", "ruleCombination", true, "combination function for rule neurons (" + settings.ruleNeuronCombination.name().toLowerCase() + ")"));
        options.addOption(new Option("rulef", "ruleTransformation", true, "transformation function for rule neurons (" + settings.ruleNeuronTransformation.name().toLowerCase() + ")"));
        options.addOption(new Option("aggf", "aggFunction", true, "aggregation function for aggregation neurons (" + settings.aggNeuronAggregation.name().toLowerCase() + ")"));

        //evaluation
        options.addOption(new Option("em", "evaluationMode", true, "evaluation is either [regression, classification, kbc] (" + "classification" + ")"));
        options.addOption(new Option("ef", "errorFunction", true, "type of error function [MSE, XEnt] (" + settings.errorAggregationFcn.name() + settings.errorFunction.name() + ")"));

        //compression
        options.addOption(new Option("iso", "isoCompression", true, "iso-value network compression (lifting), number of decimal digits (" + settings.isoDecimals + ")"));
        options.addOption(new Option("isoinits", "isoInitializations", true, "number of iso-value initializations for network compression (lifting) (" + settings.isoValueInits + ")"));
        options.addOption(new Option("isocheck", "losslessCompression", true, "lossless compression isomorphism extra check? (" + settings.structuralIsoCompression + ")"));
        options.addOption(new Option("prune", "chainPruning", true, "linear chain network pruning (" + (settings.chainPruning ? 1 : 0) + ")"));

        //todo rest of the commandline options that might be useful

        return options;
    }

    public void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(200);
        formatter.setOptionComparator(null);
        String header = "\nperform learning of a Lifted Relational Neural Network model\n";
        String footer = "\nplease refer to README or project page (https://github.com/GustikS/NeuraLogic) for further details.\n";
        formatter.printHelp("java -jar NeuraLogic.jar", header, options, footer, true);
    }

}
