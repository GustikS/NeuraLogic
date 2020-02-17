package settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import networks.computation.evaluation.values.Value;
import org.apache.commons.cli.CommandLine;
import pipelines.Pipeline;
import utils.Utilities;
import utils.exporting.Exportable;
import utils.exporting.Exporter;
import utils.generic.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Settings implements Exportable {

    //todo - uncompressed lambda template for experiments
    //todo - how to handle non-entailed examples
    //todo - dynamic restarting + learning rate progression
    //todo - various exporting
    //todo - alldiff
    //todo - learning factNeuron offsets
    //todo - special predicates

    private transient static final Logger LOG = Logger.getLogger(Settings.class.getName());

    /**
     * Stores current OS type
     */
    public static final OS os = Utilities.getOs();

    public enum OS {
        LINUX, MACOSX, WINDOWS
    }

    /**
     * Format for logging output file ./logging
     */
    public static boolean htmlLogging = false;
    /**
     * No console output
     */
    public static boolean supressConsoleOutput = false;
    /**
     * No logfile (./logging.txt) output
     */
    public static boolean supressLogFileOutput = true;
    /**
     * How detailed the log should be in general
     */
    public static Level loggingLevel = Level.FINER;
    /**
     * Colorful console output?
     */
    public static boolean customLogColors = true;

    public static String logFile = "./out/Logging";

    //------------------Dependencies (graphviz for debugging)

    public String graphvizPathLinux = "/usr/bin"; // this is where the dot typically is, if not, install graphviz
    public String graphvizPathMac = "/usr/local/bin";   //never tried OSX...
    public String graphvizPathWindows = "../resources/graphviz"; //get it if you  don't have it and want to use it!

    public String pythonPath = "/opt/miniconda3/envs/lrnn/bin/python";
    public String progressPlotterPath = "../Frontend/grid/loading_results.py";

    //------------------Exporting (i.e. output files for logging etc.)

    public BlockExporting blockExporting = BlockExporting.JSON;

    public enum BlockExporting {
        JSON, TEXT
    }

    /**
     * Cleaning all previously generated outputs before each run
     */
    public boolean cleanUpFirst = true;

    public String outDir = "./out";

    public String resultFile = outDir + "/results";

    public String settingsExportFile = outDir + "/settings.json";

    public String sourcesExportFile = outDir + "/sources.json";

    public String console = outDir + "/consoleOutput";

    public String exportDir = outDir + "/export";

    public transient Exporter exporter;

    /**
     * Outputs of these blocks will be exported into respective files
     */
    public String[] exportBlocks = {"NeuralTrainTestPipeline", "NeuralEvaluationPipe", "CrossvalidationPipeline",
            "CompressionPipe", "NetworkPruningPipe", "NeuralTrainingPipe"};

    //------------------Drawing/Debugging

    /**
     * This will prevent from searching for graphviz executable
     */
    public boolean drawing = false;

    public boolean debugPipeline = false;

    public boolean debugTemplate = false;
    public boolean debugGrounding = false;
    public boolean debugNeuralization = false;
    public boolean debugSampleTraining = false;
    public boolean debugTemplateTraining = false;

    /**
     * Just print sample output values after each recalculation of true results
     */
    public boolean debugSampleOutputs = false;

    /**
     * If on, multiple intermediate spots within the current debugging pipeline might trigger the debugger,
     * otherwise only the last consumer will trigger the debugger
     */
    public boolean intermediateDebug;

    public Detail drawingDetail = Detail.HIGH;

    public enum Detail {
        LOW, MEDIUM, HIGH
    }

    public String imageFile = outDir + "/graph";
    public String graphVizAlgorithm = "dot";
    public String imgType = "png";
    /**
     * Whether the rendered image should be exactly in the resolution of the (main) monitor
     */
    public boolean fix2ScreenSize = false;
    /**
     * Do not display images and rather store them on disk, this may be better for very large graphs which take long to render
     * if set to false (=display), no temporary files are created as we can call graphviz directly without them!
     */
    public boolean storeNotShow = false;

    /**
     * Collapse aggregation nodes into mere edge labels
     */
    public boolean compactGroundingDrawing;

    //------------------High level program setting

    public Optimize optimize = Optimize.SPEED;

    public enum Optimize {
        MEMORY, SPEED, TRADEOFF
    }

    //------------------Global structures
    /**
     * Seed for absolutely everything
     */
    public int seed = 0;
    /**
     * Global random generator
     */
    public transient Random random;
    /**
     * Global number formats for all printing
     */
    public transient static NumberFormat superDetailedNumberFormat = new DecimalFormat("#.################");

    public transient static NumberFormat detailedNumberFormat = new DecimalFormat("#.##########");

    public transient static NumberFormat shortNumberFormat = new DecimalFormat("#.##");

    //------------------Abstract Pipelines

    /**
     * A root pipeline of the actual program flow (referenced to this settings object)
     */
    public transient Pipeline root;

    /**
     * Sets up the main purpose/result of the root pipeline
     */
    public MainMode mainMode = MainMode.COMPLETE;

    public enum MainMode {
        COMPLETE,   // standard running of the whole learning process
        NEURALIZATION,  // use the program for creation of NNs only
        DEBUGGING   // run only debug of some part of the program (w.r.t. the debugXXX flags)
    }

    /**
     * Some major settings that influence pipelines creation have been changed on the run,
     * implying the need for rebuilding of every pipeline when entered (accept) during run
     */
    public boolean rebuildPipelines = false;

    /**
     * Limiting the input Sample stream to the first N samples. N <= 0 for no limit
     * the resulting number of samples may not be exact if stratification is required
     */
    public int appLimitSamples = -1;

    //------------------Grounding
    /**
     * Ground train+test example sets TOGETHER (with the same cache)
     */
    public boolean trainTestJointGrounding; //TODO implement this

    public GroundingMode groundingMode = GroundingMode.STANDARD;

    public enum GroundingMode {
        STANDARD,   //Separate independent example graphs
        SEQUENTIAL, // Ground networks are grounded in a specific given sequence (i.e. sharing only with previous examples)
        GLOBAL  //Ground networks (in a given Grounder context) may share common parts (i.e. Grounder has a cache)
    }

    /**
     * Neuron sharing ACROSS the networks
     */
    public boolean possibleNeuronSharing = false;

    /**
     * If there's no need for keeping the given sequence, ground in parallel in the given context
     */
    public boolean parallelGrounding = false;

    /**
     * Type of grounder
     */
    public GroundingAlgo grounding = GroundingAlgo.BUP;

    public enum GroundingAlgo {
        BUP, TDOWN, GRINGO
    }

    /**
     * How to aggregate 2 identical facts stated with 2 different truth values (e.g., 0.3 person(petr).; 0.9 person(petr).)
     */
    public AggregationFcn factMergeActivation = AggregationFcn.MAX;

    //-----------------Neural nets creation
    /**
     * Prune out ground rules with no support for a given query EXPLICITLY in advance (even though in a supervised pipeline, only support will be taken recursively)
     */
    public boolean explicitSupervisedGroundTemplatePruning = false;
    /**
     * Force full unsupervised network creation, even if query is provided
     */
    public boolean forceFullNetworks = false;

    /**
     * Default value for output of fact neurons (= values of ValuedFacts) if not specified by user
     */
//    public Value defaultFactValue = new ScalarValue(1);
    public Value defaultFactValue = Value.ONE;

    /**
     * Whether to setup a rule (conjunction) offset based on number of inputs (offset = - #inputs)
     * Naturally this only applies if the offset is not specified by the user in the template explicitly.
     * This offset setting has higher priority (comes first) than setting defaultRuleNeuronOffset (applied later)
     */
    public boolean ruleAdaptiveOffset = false;

    /**
     * Setup this default offset value if not explicitly specified in the template
     * 0.0 = keep no offset if not learnable, or initialize randomly if defaultRuleOffsetsLearnable
     */
    public double defaultRuleNeuronOffset = 0.0;
    /**
     * Setup this default offset value if not explicitly specified in the template
     * 0.0 = keep no offset if not learnable, or initialize randomly if defaultAtomOffsetsLearnable
     */
    public double defaultAtomNeuronOffset = 0.0;
    /**
     * Rule offsets, if not explicitly specified in the template, should be fixed or learnable?
     */
    public boolean defaultRuleOffsetsLearnable = false;     // todo if they should be learnable, we should infer their dimensions first! (ScalarValues can be broadcasted for inference only, not updates)
    /**
     * Atom offsets, if not explicitly specified in the template, should be fixed or learnable?
     */
    public boolean defaultAtomOffsetsLearnable = false;
    /**
     * A whole pipeline of all postprocessing steps
     */
    public boolean neuralNetsPostProcessing = true;    //todo at least one must be ON
    /**
     * Remove everything outside QueryNeuron's support (can appear if network have shared parts)
     */
    public boolean neuralNetsSupervisedPruning;
    /**
     * Copy out neurons fully instead of input over-mappings
     */
    public boolean copyOutInputOvermapping;
    /**
     * Create maps of neurons to their outputs in each network (individual neurons have only links to inputs)
     */
    public boolean calculateOutputLinks;
    /**
     * Remove recurrent edges from the neural networks w.r.t. some strategy
     */
    public boolean cycleBreaking;
    /**
     * Remove unnecessary parts from the networks (e.g. linear chains)
     */
    public boolean chainPruning = false;
    /**
     * Bottom-up value based sub-graph isomorphism collapsing (merging)
     */
    public boolean isoValueCompression = false;
    /**
     * If the isoValueCompression is performed, check whether the merged neurons are truly equivalent
     */
    public boolean losslessIsoCompression = false;
    /**
     * Top-down value (gradient) based sub-graph isomorphism collapsing (merging)
     */
    public boolean isoGradientCompression;
    /**
     * Detect identical input neurons and merge them into a single neuron with accumulated weight.
     */
    public boolean mergeIdenticalWeightedInputs = false;    //todo next warning this changes gradient/learning progress on diffcheck! test why!
    /**
     * If no weights are associated with the input neurons, i.e. no summing possible, prune out the identities (or keep them)
     */
    public boolean removeIdenticalUnweightedInputs = false;
    /**
     * If all neurons of the same class use the same activation, move to a single static reference (optimize memory)
     */
    public boolean collapseActivations;
    /**
     * If all the weights in a network are Scalars, move to a more efficient representation (optimize memory and speed)
     */
    public boolean collapseWeights;
    /**
     * If there are embedding constructs in the Template, expand them (copy-multiple neurons) after Network creation
     */
    public boolean expandEmbeddings;
    /**
     * What particular {@link networks.structure.metadata.states.State.Neural.Computation} will the neurons have by default?
     */
    public NeuralState neuralState = NeuralState.STANDARD;

    public enum NeuralState {
        STANDARD,
        PARENTS,
        DROPOUT,
        PAR_DROPOUT
    }

    /**
     * Do we need to count parents to use during neural iterations (DFS, BFS)?
     */
    public boolean parentCounting;

    /**
     * Each rule neuron to have the actual grounding combination string with it? (may create a lot of unique strings and consume memory)
     */
    public boolean fullRuleNeuronStrings = false;

    /**
     * Each aggregation neuron to have actual ground-head string with it?
     */
    public boolean fullAggNeuronStrings = false;

    /**
     * When pruning networks for linear chains, we compress unweighted edges as they do not provide any extra functionality.
     * With this on, we delete also weighted edges - more aggressive pruning, but may reduce network capability to learn!
     */
    public boolean pruneEvenWeightedNeurons = false;

    /**
     * Number of iterations for iso-value compression
     */
    public int isoValueInits = 1;
    /**
     * Number of decimal digits to check to consider two neurons to have the same output value
     */
    public int isoDecimals = 12;

    //-----------------Evaluation & Training
    /**
     * Calling external tool to periodically each N seconds plot training progress in a window
     */
    public int plotProgress = 1;
    /**
     * Evaluation mode: Regression vs. Classification
     */
    public boolean regression = false;

    /**
     * Include also AUC ROC and AUC PR etc. in results calculations (might be demanding if recalculated too often)
     */
    public boolean detailedResults = false;

    /**
     * Recalculate results after every N epochae
     */
    public int resultsRecalculationEpochae = 10;

    /**
     * Default algorithm for neural cache searching
     */
    public NeuronSearch neuronSearch = NeuronSearch.LINEAR;

    public enum NeuronSearch {
        LINEAR, BST, HASHMAP
    }

    /**
     * Networks size threshold for switching between neuron search algorithms
     */
    public int lin2bst = 10 ^ 3;
    /**
     * Networks size threshold for switching between neuron search algorithms
     */
    public int bst2hashmap = 10 ^ 6;

    /**
     * Parallel training with minibatches (the only truly correct parallel training). Batch size = Number of threads. SGD = 1.
     */
    public int minibatchSize = 1;

    /**
     * EXPERIMENTAL parallel training with asynchronous updates - fastest mode, but the gradients can be very wrongly overwritten (+dynamic size minibatches)
     */
    public boolean asyncParallelTraining;

    /**
     * Any parallel training, i.e. implying the need for parallel access to neurons' states
     */
    public boolean parallelTraining;

    /**
     * A single-pass weight training via streaming. This can save memory, but cannot re-iterate the data (i.e. learn in epochs)
     */
    public boolean neuralStreaming;
    /**
     * Restarting the whole training?
     */
    public int restartCount = 1;

    /**
     * Over all the restarts, how many epoch can be done at maximum.
     */
    public int maxCumEpochCount = 1000;

    /**
     * Shuffle samples before neural training (only turn off for debugging purposes)
     */
    public boolean shuffleBeforeTraining = true;

    /**
     * with Minibatch = Shuffle samples with each epoch to create different minibatches.
     * with SGD = Shuffle samples with each epoch to pass the samples in different orders (=shuffle even for the SGD mode)
     */
    public boolean shuffleEachEpoch = true;

    public boolean islearnRateDecay = false;

    public InitSet initializer = InitSet.SIMPLE;

    public enum InitSet {
        SIMPLE, GLOROT
    }

    public InitDistribution initDistribution = InitDistribution.UNIFORM;

    public enum InitDistribution {
        UNIFORM, CONSTANT
    }

    /**
     * Range of uniformly distributed numbers for weights initialization (2 = [-1,1])
     */
    public double randomInitScale = 2;

    /**
     * Check whether neurons are not mostly saturated in the range of their activation functions
     */
    public boolean checkNeuronSaturation = true;

    /**
     * What percentage of neurons in a network must be saturated to consider it a problem
     */
    public double saturationPercentage = 0.1;

    /**
     * Constant value to initialize all weights with, used for debugging purposes (with Constant Distribution) only!
     */
    public double constantInitValue = 0.1;

    public double initLearningRate = 0.01;

    public double dropoutRate = 0.0;

    public OptimizerSet optimizer = OptimizerSet.ADAM;

    public enum OptimizerSet {
        SGD, ADAM
    }

    /**
     * Percentual size of validation set separated from training set
     */
    public double validationSet = 0.1;

    public DropoutMode dropoutMode = DropoutMode.LIFTED_DROPCONNECT;

    public enum DropoutMode {
        DROPOUT, // = drop neurons
        DROPCONNECT, // = drop edges
        LIFTED_DROPCONNECT // = drop weights (= drop many different edges)
    }

    /**
     * Whether to search for best threshold splitting between positive and negative samples in binary classification
     * that maximizes accuracy (to be reported then), instead of simple 0.5 threshold
     */
    public boolean calculateBestThreshold = true;   //todo now only do with recalculation of true results!

    public ErrorFcn errorFunction = ErrorFcn.SQUARED_DIFF;

    public AggregationFcn errorAggregationFcn = AggregationFcn.AVG;

    public ActivationFcn atomNeuronActivation = ActivationFcn.SIGMOID;
    public AggregationFcn aggNeuronActivation = AggregationFcn.AVG;
    public ActivationFcn ruleNeuronActivation = ActivationFcn.SIGMOID;
    public ActivationFcn negation = ActivationFcn.REVERSE;

    public enum ActivationFcn {
        SIGMOID, TANH, SIGNUM, LUKASIEWICZ, RELU, IDENTITY, REVERSE;
    }

    public enum ErrorFcn {
        SQUARED_DIFF, ABS_DIFF, CROSSENTROPY;
    }

    public enum AggregationFcn {
        AVG, MAX, MIN, SUM
    }

    public IterationMode iterationMode = IterationMode.TOPOLOGIC;

    public enum IterationMode {
        TOPOLOGIC, DFS_RECURSIVE, DFS_STACK, BFS
    }

    /**
     * Percentage of samples from train-set used for training, 1 = empty validation set
     */
    public double trainValidationPercentage = 1;

    /**
     * After neural training, i.e. finding the best set of parameters and error values,
     * should we return the state of all the parameters (Weights) back to the state before training?
     */
    public boolean undoWeightTrainingChanges;

    //-----------------Structure Learning
    public boolean allowStructureLearning = false;
    public boolean structureLearning;

    //-----------------Source files

    /**
     * Default path to input  file with json specification of Sources (SourceFiles)
     */
    public String sourcesFile = "sources.json";
    /**
     * Default path to input file with all the settings
     */
    public String settingsFile = "settings.json";

    /**
     * Format of the input (file) readers is for plain-text parsers (and not e.g. xml)
     */
    public boolean plaintextInput;
    /**
     * Are the data sources files? (not e.g. streams from memory/internet)
     */
    public boolean sourceFiles = true;

    /**
     * All the sources have default names in this path? Allows for short/simple commandline setup
     */
    public boolean sourcePathProvided = false;
    public String sourcePath = ".";
    public String templateFile = "template.txt";    //todo remove/unify the txt
    public String trainExamplesFile = "trainExamples.txt";
    /**
     * Alternative file name
     */
    public String trainExamplesFile2 = "examples";
    public String testExamplesFile = "testExamples.txt";
    public String trainQueriesFile = "trainQueries.txt";
    /**
     * Alternative file name
     */
    public String trainQueriesFile2 = "queries";
    public String testQueriesFile = "testQueries.txt";

    public String foldsPrefix = "fold";

    public String queryExampleSeparator = ":-";

    //----------------Crossvaldiation

    /**
     * Can enforce crossvalidation instead of pure training as default
     */
    public boolean crossvalidation;
    /**
     * If false, the x-val folds will always be the same!
     */
    public boolean shuffleBeforeFoldSplit = true;
    /**
     * Number of folds for crossvaldiation
     */
    public int foldsCount = 5;
    /**
     * Assemble folds w.r.t. class distribution
     */
    public boolean stratification = true;
    public boolean exportFolds = true;
    /**
     * Are the train folds to be completely isolated, even though they share common subsets? (test folds are always isolated).
     * If so, then many operations (e.g. grounding) will be unnecessarily repeated for these common subsets.
     */
    public boolean trainFoldsIsolation = false;

    /**
     * Same template provided for all folds?
     */
    public boolean commonTemplate;

    //----------------Template Transformations

    /**
     * Apply all metadata taken from sources
     */
    public boolean processMetadata = true;
    /**
     * Build template graph structure
     */
    public boolean graphTemplate = true;
    /**
     * Reduce template graph size (e.g. linear chains)
     */
    public boolean reduceTemplate = true;
    /**
     * If the template contains facts, infer all other possible true facts as a preprocessing step (to save some time inferring the same things over and over later)
     */
    public boolean inferTemplateFacts = true;
    /**
     * In advance of grounding (theorem proving), remove rules that are irrelevant to the given query (with no chance to be in support)
     */
    public boolean supervisedTemplateGraphPruning = false;   //todo measure if this actually helps

    //----------------Learning Samples

    /**
     * There is exactly 1 query per each example (allows for some speedup in merging)
     */
    public boolean oneQueryPerExample = true;
    /**
     * Queries and Examples are 1-1 and also ordered correspondingly (allows to just merge the 2 streams without terminating them)
     */
    public boolean queriesAlignedWithExamples;

    public double defaultSampleImportance = 1.0;
    public String sampleIdPrefix = "s_";
    public String queriesBatchPrefix = "b_";


    /**
     * Stores dynamically inferred settings
     */
    public class Inferred {
        //todo store dynamically inferred settings here

    }

    /**
     * TODO Setup globally default settings here
     */
    public Settings() {
    }

    /**
     * This will possibly overwrite all the previously setup fields!
     * I.e. commandline options have higher priority!
     *
     * @param cmd
     * @return
     */
    public Settings setupFromCommandline(CommandLine cmd) {
        Settings settings = this;

        if (cmd.hasOption("settings")) {
            String _settingsPath = cmd.getOptionValue("settings");
            settings = loadFromJson(_settingsPath);
        }

        if (cmd.hasOption("out")) {
            settings.outDir = cmd.getOptionValue("out");
        }

        if (cmd.hasOption("xval")) {
            String _xval = cmd.getOptionValue("xval", String.valueOf(foldsCount));
            settings.foldsCount = Integer.parseInt(_xval);
        }

        if (cmd.hasOption("seed")) {
            String _seed = cmd.getOptionValue("seed", String.valueOf(seed));
            settings.random = new Random(Integer.parseInt(_seed));
        }

        if (cmd.hasOption("groundingAlgorithm")) {
            String _groundingAlgorithm = cmd.getOptionValue("groundingAlgorithm", grounding.name());
            switch (_groundingAlgorithm) {
                case "BUp":
                    settings.grounding = GroundingAlgo.BUP;
                    break;
                case "TDown":
                    settings.grounding = GroundingAlgo.TDOWN;
                    break;
                case "Gringo":
                    settings.grounding = GroundingAlgo.GRINGO;
                    break;
            }
        }

        if (cmd.hasOption("groundingMode")) {
            String _groundingMode = cmd.getOptionValue("groundingMode", "normal");
            switch (_groundingMode) {
                case "normal":
                    settings.groundingMode = GroundingMode.STANDARD;
                    break;
                case "sequential":
                    settings.groundingMode = GroundingMode.SEQUENTIAL;
                    break;
                case "global":
                    settings.groundingMode = GroundingMode.GLOBAL;
                    break;
            }
        }

        if (cmd.hasOption("weightInit")) {
            String _weightInit = cmd.getOptionValue("weightInit", String.valueOf(initDistribution));
            switch (_weightInit.toLowerCase()) {
                case "uniform":
                    settings.initDistribution = InitDistribution.UNIFORM;
                    break;
                case "constant":
                    settings.initDistribution = InitDistribution.CONSTANT;
                    break;
                default:
                    LOG.severe("unrecognized init distribution: " + _weightInit);
            }
        }

        if (cmd.hasOption("optimizer")) {
            String _optimizer = cmd.getOptionValue("optimizer", String.valueOf(optimizer));
            switch (_optimizer.toLowerCase()) {
                case "sgd":
                    settings.optimizer = OptimizerSet.SGD;
                    break;
                case "adam":
                    settings.optimizer = OptimizerSet.ADAM;
                    break;
                default:
                    LOG.severe("unrecognized optimizer: " + _optimizer);
            }
        }

        if (cmd.hasOption("learningRate")) {
            String _learningRate = cmd.getOptionValue("learningRate", String.valueOf(initLearningRate));
            settings.initLearningRate = Double.parseDouble(_learningRate);
        }

        if (cmd.hasOption("trainingSteps")) {
            String _trainingSteps = cmd.getOptionValue("trainingSteps", String.valueOf(maxCumEpochCount));
            settings.maxCumEpochCount = Integer.parseInt(_trainingSteps);
        }

        if (cmd.hasOption("evaluationMode")) {
            String _evaluationMode = cmd.getOptionValue("evaluationMode", "classification");
            switch (_evaluationMode) {
                case "classification":
                    settings.regression = false;
                    break;
                case "regression":
                    settings.regression = true;
                    break;
            }
        }

        if (cmd.hasOption("errorFunction")) {
            String _errorFunction = cmd.getOptionValue("errorFunction", "MSE");
            switch (_errorFunction) {
                case "MSE":
                    settings.errorFunction = ErrorFcn.SQUARED_DIFF;
                    settings.errorAggregationFcn = AggregationFcn.AVG;
                    break;
                case "XEnt":
                    LOG.severe("XEnt not yet implemented");
                    break;
            }
        }

        if (cmd.hasOption("sourcePath")) {
            settings.sourcePathProvided = true;
        }

        if (cmd.hasOption("mode")) {
            String _mode = cmd.getOptionValue("mode", String.valueOf(mainMode));
            switch (_mode.toLowerCase()) {
                case "complete":
                    settings.mainMode = MainMode.COMPLETE;
                    break;
                case "neuralization":
                    settings.mainMode = MainMode.NEURALIZATION;
                    break;
                case "debug":
                    settings.mainMode = MainMode.DEBUGGING;
                    break;
            }
        }

        if (cmd.hasOption("debug")) {
            String _debug = cmd.getOptionValue("debug");
            switch (_debug) {
                case "template":
                    settings.debugTemplate = true;
                    break;
                case "grounding":
                    settings.debugGrounding = true;
                    break;
                case "neuralization":
                    settings.debugNeuralization = true;
                    break;
                case "samples":
                    settings.debugSampleTraining = true;
                    break;
                case "model":
                    settings.debugTemplateTraining = true;
                    break;
            }
        }

        if (cmd.hasOption("limitExamples")) {
            String _limit = cmd.getOptionValue("limitExamples");
            settings.appLimitSamples = Integer.parseInt(_limit);
        }

        if (cmd.hasOption("isoCompression")) {
            String _isoCompression = cmd.getOptionValue("isoCompression", String.valueOf(isoDecimals));
            settings.isoDecimals = Integer.parseInt(_isoCompression);
            if (settings.isoDecimals > 0) {
                settings.neuralNetsPostProcessing = true;
                settings.isoValueCompression = true;
            } else {
                settings.isoValueCompression = false;
            }
        }

        if (cmd.hasOption("isoInitializations")) {
            String _isoInits = cmd.getOptionValue("isoInitializations");
            settings.isoValueInits = Integer.parseInt(_isoInits);
        }

        if (cmd.hasOption("losslessCompression")) {
            String _losslessCompression = cmd.getOptionValue("losslessCompression");
            settings.losslessIsoCompression = Integer.parseInt(_losslessCompression) > 0;
        }

        if (cmd.hasOption("chainPruning")) {
            String _pruning = cmd.getOptionValue("chainPruning", String.valueOf(chainPruning));
            int prune = Integer.parseInt(_pruning);
            if (prune > 0) {
                settings.neuralNetsPostProcessing = true;
                settings.chainPruning = true;
            } else {
                settings.chainPruning = false;
            }
        }

        //todo fill all the most useful settings
        return settings;
    }


    /**
     * Check for banned combinations here
     *
     * @return
     */
    public Pair<Boolean, String> validate() {
        boolean valid = true;
        StringBuilder message = new StringBuilder();

        if (groundingMode == GroundingMode.SEQUENTIAL) {
            if (parallelGrounding)
                valid = false;
            message.append("Not possible");
        }
        if (!oneQueryPerExample) {
            if (explicitSupervisedGroundTemplatePruning)
                valid = false;
        }

        if (stratification && regression) {
            message.append("stratification not possible with regression");
            valid = false;
        }
        //todo more validation and inference of settings

        return new Pair(valid, message);
    }

    /**
     * Infer all remaining settings from the given
     */
    public void infer() {
        random = new Random(seed);

        if (reduceTemplate) graphTemplate = true;
        parentCounting = (iterationMode != IterationMode.TOPOLOGIC);

        if (dropoutRate == 0 && !parentCounting) {
            neuralState = NeuralState.STANDARD;
        } else if (dropoutRate == 0 && parentCounting) {
            neuralState = NeuralState.PARENTS;
        } else if (dropoutRate > 0 && parentCounting) {
            neuralState = NeuralState.PAR_DROPOUT;
        }

        if (groundingMode == GroundingMode.SEQUENTIAL) {
            forceFullNetworks = true;   //if we sequentially add new facts/rules, and then after grounding we take just the diff, the rules might not be connected, i.e. we need to turn them all blindly to neurons.
            possibleNeuronSharing = true;
        }
        if (groundingMode == GroundingMode.GLOBAL) {
            possibleNeuronSharing = true;
            neuralNetsSupervisedPruning = false;
        }

        //in case the outDir changed...
        resultFile = outDir + "/results";
        settingsExportFile = outDir + "/settings.json";
        sourcesExportFile = outDir + "/sources.json";
        console = outDir + "/consoleOutput";
        exportDir = outDir + "/export";

        if (chainPruning || isoValueCompression || neuralNetsSupervisedPruning || copyOutInputOvermapping || isoGradientCompression || mergeIdenticalWeightedInputs || removeIdenticalUnweightedInputs || cycleBreaking || collapseWeights || expandEmbeddings)
            neuralNetsPostProcessing = true;
        else
            neuralNetsPostProcessing = false;

//        resultsRecalculationEpochae = maxCumEpochCount / 100;
        //todo rest

        finish();
    }

    /**
     * Steps to be performed once the settings are totally complete, i.e. after all the inference and validation
     */
    private void finish() {
        exporter = new Exporter(this);

        if (cleanUpFirst) {
            exporter.deleteDir(new File(exportDir));
        }

        exporter.exportSettings(this);
    }

    public void importFromCSV(String inPath) {

    }

    public Settings loadFromJson(String inPath) {
        InstanceCreator<Settings> creator = new InstanceCreator<Settings>() {
            public Settings createInstance(Type type) {
                return Settings.this;
            }
        };
        Gson gson = new GsonBuilder().registerTypeAdapter(Settings.class, creator).create();
        try {
            String json = new String(Files.readAllBytes(Paths.get(inPath)));
            Settings settings = gson.fromJson(json, Settings.class);
            return settings;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void exportToCSV(String outPath) {

    }

}
