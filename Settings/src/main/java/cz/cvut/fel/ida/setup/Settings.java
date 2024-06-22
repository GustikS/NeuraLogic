package cz.cvut.fel.ida.setup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

//import cz.cvut.fel.ida.ml.exporting.Exportable;
//import cz.cvut.fel.ida.ml.exporting.Exporter;

/**
 * Created by gusta on 8.3.17.
 */
public class Settings implements Serializable {

    //todo - uncompressed lambda template for experiments
    //todo - how to handle non-entailed examples
    //todo - dynamic restarting + learning rate progression
    //todo - learning factNeuron offsets?

    private transient static final Logger LOG = Logger.getLogger(Settings.class.getName());

    /**
     * Stores current OS type
     */
    public static final OS os = getOs();

    private static File referenceSettingsFile;

    /**
     *
     */
    public String getChangesFromReference() {
        //todo next diff vs. some referenceSettingsFile
        return "defaultParams";
    }

    public enum OS {
        LINUX, MACOSX, WINDOWS
    }

    public static String inputFilesSuffix = ".txt";
//public static String inputFilesSuffix = "";

    public static OS getOs() {
        String osName = System.getProperty("os.name").replaceAll("\\s", "");
        if (osName.contains("Windows")) {
            return OS.WINDOWS;
        } else if (osName.contains("MacOSX")) {
            return OS.MACOSX;
        } else if (osName.contains("Linux")) {
            return OS.LINUX;
        }
        return OS.LINUX;
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

    /**
     * Path to output log file
     */
    public static String logFile = "./out/Logging";

    /**
     * Seed for absolutely everything (turned into static to pass even into distant packages, unfortunately)
     */
    public static int seed = 0;
    //------------------Builders

    public static Settings forFastTest() {
        Settings setting = new Settings();
        setting.drawing = false;
        setting.plotProgress = -1;
        setting.appLimitSamples = 2;    //todo check why this doesn work in KB mode
        setting.seed = 0;
        setting.maxCumEpochCount = 2;
        setting.mainMode = MainMode.COMPLETE;
//        setting.neuralNetsPostProcessing = false;
//        setting.isoValueCompression = false;
//        setting.chainPruning = false;
        setting.outDir = Settings.logFile;
        return setting;
    }

    public static Settings forMediumTest() {
        Settings setting = new Settings();
        setting.drawing = false;
        setting.plotProgress = -1;
        setting.appLimitSamples = -1;
        setting.maxCumEpochCount = 100;
        setting.resultsRecalculationEpochae = 100;
        setting.seed = 0;
        setting.outDir = Settings.logFile;
        return setting;
    }

    public static Settings forSlowTest() {
        Settings setting = new Settings();
        setting.drawing = false;
        setting.plotProgress = -1;
        setting.appLimitSamples = -1;
        setting.seed = 0;
        setting.outDir = Settings.logFile;
        return setting;
    }

    public static Settings forInteractiveTest() {
        Settings setting = new Settings();
        setting.drawing = true;
        setting.plotProgress = 1;
        setting.appLimitSamples = -1;
        setting.seed = 0;
        setting.outDir = Settings.logFile;
        return setting;
    }

    //------------------Dependencies (graphviz for debugging)

    public String graphvizPath = null;

    public String pythonPath = "python";

    public String progressPlotterPath = "../Frontend/grid/loading_results.py";

    //------------------Exporting (i.e. output files for logging etc.)

    public ExportFileType exportType = ExportFileType.JSON;

    public enum ExportFileType {
        JSON, TEXT, JAVA
    }

    /**
     * Cleaning all previously generated outputs before each run
     */
    public boolean cleanUpFirst = true;

    public String outDir = "./target/out";

    public String resultFile;

    public String settingsExportFile;

    public String sourcesExportFile;

    public String console;

    public String exportDir;

//    public transient Exporter exporter;

    /**
     * Outputs of these blocks will be exported into respective files
     */
    public String[] exportBlocks = {
            "TrainTestPipeline",        //exports TrainTestResults (default output)
            "NeuralTrainTestPipeline",  //exports TrainTestResults (default output)
            "NeuralEvaluationPipe",     //exports Results (default output)
            "CrossvalidationPipeline",  //exports (aggregated) TrainTestResults (default output)
            "CompressionPipe",          //exports IsoValueNetworkCompressor = compression statistics (overriden!, default output would be Stream<NeuralProcessingSample>)
            "NetworkPruningPipe",       //exports LinearChainReducer = pruning statistics (overriden!, default output would be Stream<NeuralProcessingSample>)
            "NeuralTrainingPipe",       //exports stats from {@link TrainingStrategy} (through ExportingPipe!, default would be Pair<NeuralModel, Progress>)
            "GroundingPipeline",        //exports stats from {@link Grounder} (through ExportingPipe!, default output would be Stream<GroundingSample>)
            "NeuralizationPipeline",    //exports stats from {@link Neuralizer} (through ExportingPipe!, default output would be Stream<NeuralSample>)
//            "LearningPipeline",         //exports Pair<Pair<Template, NeuralModel>, Progress> (default output)  -> replaced with an explicit TemplateExporter
            "LearningSchemePipeline",    //exports PipelineTiming<Results> (default output = Results + Timing due to Pipeline default functionality)
            "NeuralEvaluationPipe"
    };

    //------------------Drawing/Debugging

    /**
     * Making this false will prevent from searching for graphviz executable
     */
    public boolean drawing = false;
    /**
     * Default debugging (=all) is on - fast switch to debug everything
     */
    public boolean debugAll = false;

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

    /**
     * Debugger will export the final output as a List of objects into json file
     */
    public boolean debugExporting = true;

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
        MEMORY, SPEED, TRADEOFF     //todo next actually infer some fields with these presets
    }

    //------------------Global structures

    /**
     * Global random generator
     */
    public transient Random random;

    private transient static DecimalFormatSymbols decimalFormatSymbol = new DecimalFormatSymbols(Locale.US);

    static {
        decimalFormatSymbol.setInfinity("Infinity");
    }

    /**
     * Global number formats for all printing
     */
    public transient static NumberFormat superDetailedNumberFormat = new DecimalFormat("#.################", decimalFormatSymbol);

    public transient static NumberFormat detailedNumberFormat = new DecimalFormat("#.##########", decimalFormatSymbol);

    public transient static NumberFormat shortNumberFormat = new DecimalFormat("#.##", decimalFormatSymbol);

    /**
     * Default numeric Value precision for printing
     */
    public transient static NumberFormat defaultNumberFormat = shortNumberFormat;

    //------------------Abstract Pipelines

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

    public GroundingMode groundingMode = GroundingMode.INDEPENDENT;

    public enum GroundingMode {
        INDEPENDENT,   //Separate independent example graphs
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
    public CombinationFcn factMergeActivation = CombinationFcn.MAX;

    /**
     * We commonly squish duplicits rule groundings.
     * But sometimes we might wish duplicit ground bodies to be aggregated (e.g. to get SUM of repeated elements)
     */
    public boolean uniqueGroundingsOnly = true;

    //-----------------Neural nets creation
    /**
     * When two queries reference the same output neuron in the same example but with possibly different target values
     */
    public boolean aggregateConflictingQueries = true;
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
    public double defaultFactValue = 1.0;

    /**
     * Whether to setup a rule (conjunction) offset based on number of inputs (offset = - #inputs)
     * Naturally this only applies if the offset is not specified by the user in the template explicitly.
     * This offset setting has higher priority (comes first) than setting defaultRuleNeuronOffset (applied later)
     */
    public boolean ruleAdaptiveOffset = false;  //todo next test this

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
    public boolean neuralNetsPostProcessing = true;
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
    public boolean chainPruning = true;
    /**
     * Bottom-up value based sub-graph isomorphism collapsing (merging)
     */
    public boolean isoValueCompression = true;
    /**
     * If the isoValueCompression is performed, check whether the merged neurons are structurally (not just functionally) equivalent.
     * This is mostly for theoretical purposes and not typically needed in practice where we don't care about the true equivalence
     */
    public boolean structuralIsoCompression = false;
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
     * What particular neural computation state will the neurons have by default?
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
     * When pruning, prune linear chains only for neurons with identity activations (and Aggregation neurons)
     * i.e. do not change the network function at all
     */
    public boolean pruneOnlyIdentities = false;
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
     * Calling external tool to periodically each N seconds plot training progress in a window (-1 == off)
     */
    public int plotProgress = -1;

    /**
     * The training task type - this flag influences, e.g., neural sample postprocessing
     */
    public ResultsType trainOnlineResultsType = ResultsType.CLASSIFICATION;
    public ResultsType trainRecalculationResultsType = ResultsType.DETAILEDCLASSIFICATION;
    public ResultsType validationResultsType = ResultsType.DETAILEDCLASSIFICATION;
    public ResultsType testResultsType = ResultsType.DETAILEDCLASSIFICATION;

    public enum ResultsType {
        REGRESSION,     // most basic type - only the actual errorFunction Value is calculated
        CLASSIFICATION,     // adds metrics such as Accuracy, precision etc.
        DETAILEDCLASSIFICATION,     // Include also AUC ROC and AUC PR etc. in results calculations (might be demanding if recalculated too often) in RECALCULATION
        KBC // Calculate also the HITs/MRR metric etc. in KBC mode
    }

    /**
     * Alternative calculation from Wilcoxon
     */
    public boolean alternativeAUC = false;
    /**
     * Recalculate true and validation results after every N epochae
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
    public int minibatchSize = 1;       //todo next test

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
     * Restarting the whole training? Good e.g. in combination with earlyStopping
     */
    public int restartCount = 5;

    /**
     * Applies a DynamicRestartingStrategy with adaptive early stopping, or simple fixed maxCumEpochCount if off
     */
    public boolean earlyStopping = false;

    /**
     * Over all the restarts, how many epoch can be done at maximum.
     */
    public int maxCumEpochCount = 3000;

    /**
     * Number of epochae to take into account when calculating moving averages for loss decay detection
     */
    public int earlyStoppingPatience = 100;

    /**
     * Shuffle samples before neural training (only turn off for debugging purposes)
     */
    public boolean shuffleBeforeTraining = true;

    /**
     * with Minibatch = Shuffle samples with each epoch to create different minibatches.
     * with SGD = Shuffle samples with each epoch to pass the samples in different orders (=shuffle even for the SGD mode)
     */
    public boolean shuffleEachEpoch = true;

    /**
     * Learning rate decay on/off
     */
    public boolean islearnRateDecay = false;    //todo next

    /**
     * Apply the learning rate decay ever N steps
     */
    public int decaySteps = 100;

    /**
     * Learning rate decay coefficient
     */
    public double learnRateDecay = 0.95;

    /**
     * Form of learning rate decay progression
     */
    public DecaySet decaySet = DecaySet.GEOMETRIC;

    public enum DecaySet {
        ARITHMETIC, GEOMETRIC
    }

    /**
     * How to initialize random weight values
     */
    public InitSet initializer = InitSet.SIMPLE;

    public enum InitSet {
        SIMPLE, GLOROT, HE
    }

    /**
     * Distribution to be drawn from
     */
    public InitDistribution initDistribution = InitDistribution.UNIFORM;

    public enum InitDistribution {
        UNIFORM, CONSTANT, NORMAL, LONGTAIL
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
    public double saturationPercentage = 0.3;

    /**
     * Constant value to initialize all weights with, used for debugging purposes (with Constant Distribution) only!
     */
    public double constantInitValue = 0.1;

    /**
     * Default (init) learning rate possibly altered during learning by decay strategies or in the optimizer setter!
     */
    public double initLearningRate = 0.001;

    public double dropoutRate = 0.0;    //todo test

    private OptimizerSet optimizer = OptimizerSet.ADAM;

    public enum OptimizerSet {
        SGD, ADAM
    }

    public OptimizerSet getOptimizer() {
        return optimizer;
    }

    /**
     * The default/initial learning rate is bound to the optimizer - that's why access through setter
     *
     * @param iOptimizer
     */
    public void setOptimizer(OptimizerSet iOptimizer) {
        switch (iOptimizer) {
            case SGD:
                initLearningRate = 0.1;
                break;
            case ADAM:
                initLearningRate = 0.0001;
                break;
        }
        this.optimizer = iOptimizer;
    }

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
    public boolean calculateBestThreshold = true;

    public ErrorFcn errorFunction = ErrorFcn.SQUARED_DIFF;

    /**
     * If set to true, it will automatically infer (override) output neuron's activation AND error fcn based on the learning setting
     * - i.e. evaluationMode (classification/regression/KBC)
     * - e.g. it will set softmax+crossentropy if multinomial classification is detected (and sigmoid for binary)
     */
    public boolean inferOutputFcns = true;

    /**
     * Merge the last activation fcn and the errorFcn into a single function
     * - e.g. squish softmax + crossentropy into softentropy for optimization and numerical stability
     * - this is generally desirable to keep true
     */
    public boolean squishLastLayer = true;

    /**
     * Include also the actual LRNN Query predicate as input to the corruptions
     */
    public boolean hitsReifyPredicate = false;

    /**
     * Definition of the corrupted tuples
     */
    public HitsCorruption hitsCorruption = HitsCorruption.ONE_DIFF;

    public enum HitsCorruption {
        ONE_SAME,    // = arbitrary change from the etalon with at least 1 common element
        ONE_DIFF,   // = exactly 1 element differs
        ALL_DIFF;   // = each query is compared against all corruptions
    }

    /**
     * Store precalculated corrupted tuples for each valid query for speedup - this might require a lot of memory!
     */
    public boolean storeHitsCorruptions = true;

    /**
     * Whether to keep some term/predicate should be preserved w.r.t. corruptions
     */
    public HitsPreservation hitsPreservation = HitsPreservation.NONE;

    public enum HitsPreservation {
        NONE,
        FIRST_STAYS,    // = first element must be same, rest arbitrary
        MIDDLE_STAYS,   // = exactly 3 elements, middle one stays
    }

    /**
     * How to solve ordering of queries in the case of the same predicted values
     */
    public HitsClashes hitsClashes = HitsClashes.AVG;

    public enum HitsClashes {
        AVG, NONE, RANDOM;
    }

    /**
     * Merge stored cache from trainResults into ValidationResults and then to TestResults
     * Use e.g. to pass corrupted (and valid) queries from train set to validation set for HITs in KBC
     */
    public boolean passResultsCache = false;


    public CombinationFcn errorAggregationFcn = CombinationFcn.AVG;

    public CombinationFcn ruleNeuronCombination = CombinationFcn.SUM;
    public TransformationFcn ruleNeuronTransformation = TransformationFcn.TANH;
    public CombinationFcn atomNeuronCombination = CombinationFcn.SUM;
    public TransformationFcn atomNeuronTransformation = TransformationFcn.TANH;
    public CombinationFcn aggNeuronAggregation = CombinationFcn.AVG;    // this should only be from the Aggregation subset!
    public TransformationFcn softNegation = TransformationFcn.REVERSE;

    public enum CombinationFcn {
        AVG, MAX, MIN, SUM, COUNT,  //AggregationFcn
        PRODUCT, ELPRODUCT, SOFTMAX, SPARSEMAX, CROSSSUM, CONCAT, COSSIM    //CombinationFcn
    }

    public enum TransformationFcn {
        SIGMOID, TANH, SIGNUM, LUKASIEWICZ, RELU, LEAKYRELU, REVERSE, INVERSE, EXP, LOGARITHM, SQRT,     //ActivationFcn
        IDENTITY, TRANSP, NORM, SOFTMAX, SPARSEMAX, MAX, MIN;    //TransformationFcn
    }

    public static CombinationFcn parseCombination(String combination) {
        switch (combination.toLowerCase()) {
            // Aggregation
            case "avg":
                return CombinationFcn.AVG;
            case "max":
                return CombinationFcn.MAX;
            case "min":
                return CombinationFcn.MIN;
            case "sum":
                return CombinationFcn.SUM;
            case "count":
                return CombinationFcn.COUNT;
            // Combination
            case "product":
                return CombinationFcn.PRODUCT;
            case "elproduct":
                return CombinationFcn.ELPRODUCT;
            case "softmax":
                return CombinationFcn.SOFTMAX;
            case "sparsemax":
                return CombinationFcn.SPARSEMAX;
            case "crosssum":
                return CombinationFcn.CROSSSUM;
            case "concat":
                return CombinationFcn.CONCAT;       // can also be aggregation now
            case "cossim":
                return CombinationFcn.COSSIM;
            default:
                throw new RuntimeException("Unable to parse combination function from: " + combination);
        }
    }

    public static TransformationFcn parseTransformation(String transformation) {
        switch (transformation.toLowerCase()) {
            // ElementWise Activation
            case "sigmoid":
                return TransformationFcn.SIGMOID;
            case "sigm":
                return TransformationFcn.SIGMOID;
            case "tanh":
                return TransformationFcn.TANH;
            case "signum":
                return TransformationFcn.SIGNUM;
            case "relu":
                return TransformationFcn.RELU;
            case "leakyrelu":
                return TransformationFcn.LEAKYRELU;
            case "identity":
                return TransformationFcn.IDENTITY;
            case "lukasiewicz":
                return TransformationFcn.LUKASIEWICZ;
            case "exp":
                return TransformationFcn.EXP;
            case "sqrt":
                return TransformationFcn.SQRT;
            case "inverse":
                return TransformationFcn.INVERSE;
            case "reverse":
                return TransformationFcn.REVERSE;
            case "log":
                return TransformationFcn.LOGARITHM;
            // Transformation
            case "softmax":
                return TransformationFcn.SOFTMAX;
            case "sparsemax":
                return TransformationFcn.SPARSEMAX;
            case "transpose":
                return TransformationFcn.TRANSP;
            case "norm":
                return TransformationFcn.NORM;
            case "layernorm":
                return TransformationFcn.NORM;
            default:
                throw new RuntimeException("Unable to parse transformation function from: " + transformation);
        }
    }

    public enum ErrorFcn {
        SQUARED_DIFF, ABS_DIFF, CROSSENTROPY, SOFTENTROPY;
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
    public boolean undoWeightTrainingChanges = false;

    public enum ModelSelection {
        ERROR, ACCURACY, DISPERSION, AUCroc, AUCpr
    }

    /**
     * Select the best model (from training) based on which metric?
     */
    public ModelSelection modelSelection = ModelSelection.ERROR;

    public enum DataSelection {
        ONLINETRAIN, TRUETRAIN, VALIDATION
    }

    /**
     * Evaluate the best model (e.g. for early stopping) preferably based on what evaluations?
     */
    public DataSelection dataSelection = DataSelection.VALIDATION;

    public boolean exportTrainedModel = true;

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

    public String templateFile = "template" + inputFilesSuffix;
    public String templateFile2 = Paths.get("templates", "template") + inputFilesSuffix;

    public String mergedTemplatesSuffix = "_merged" + inputFilesSuffix;

    public String trainExamplesFile = "trainExamples" + inputFilesSuffix;
    /**
     * Alternative file name
     */
    public String trainExamplesFile2 = "examples" + inputFilesSuffix;

    public String valExamplesFile = "valExamples" + inputFilesSuffix;

    public String testExamplesFile = "testExamples" + inputFilesSuffix;

    public String trainQueriesFile = "trainQueries" + inputFilesSuffix;
    /**
     * Alternative file name
     */
    public String trainQueriesFile2 = "queries" + inputFilesSuffix;

    public String valQueriesFile = "valQueries" + inputFilesSuffix;

    public String testQueriesFile = "testQueries" + inputFilesSuffix;

    public String foldsPrefix = "fold_";

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
     * Assemble folds (e.g. train-val split) w.r.t. class distribution
     */
    public boolean stratification = true;

    public boolean exportFolds = true;
    /**
     * Are the train folds to be completely isolated, even though they share common subsets? (test folds are always isolated).
     * If so, then many operations (e.g. grounding) will be unnecessarily repeated for these common subsets.
     */
    public boolean trainFoldsIsolation = false;

    //----------------Template Transformations

    /**
     * Check if the template is stratified w.r.t. negation (no cycles with a negated edge)
     */
    public boolean checkStratification = true;
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
    public boolean reduceTemplate = false;
    /**
     * If the template contains facts, infer all other possible true facts as a preprocessing step (to save some time inferring the same things over and over later)
     * AND also preprocess rules into more efficient (ClauseC) indexed structures and store for later
     */
    public boolean preprocessTemplateInference = true;
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
     * Stores dynamically inferred settings and values
     */
    public transient Inferred inferred = new Inferred();

    /**
     * Stores dynamically inferred settings
     */
    public class Inferred {
        //todo store dynamically inferred settings here

        /**
         * The maximal index of any weight created during the process
         * - inferred after template/sample processing (incl. grounding)
         */
        public AtomicInteger maxWeightCount = new AtomicInteger(0);

    }

    /**
     * Setup globally default settings here -> it is initialized right in the fields
     * and inferred and validated later
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

        if (cmd.hasOption("logColors")) {
            String logColors = cmd.getOptionValue("logColors");
            Settings.customLogColors = Integer.parseInt(logColors) > 0;
        }

        if (cmd.hasOption("settingsFile")) {
            String _settingsPath = cmd.getOptionValue("settings");
            settings = updateFromJson(Paths.get(_settingsPath));
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
            settings.seed = Integer.parseInt(_seed);
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
                    settings.groundingMode = GroundingMode.INDEPENDENT;
                    break;
                case "sequential":
                    settings.groundingMode = GroundingMode.SEQUENTIAL;
                    break;
                case "global":
                    settings.groundingMode = GroundingMode.GLOBAL;
                    break;
            }
        }

        if (cmd.hasOption("distribution")) {
            String _distr = cmd.getOptionValue("distribution");
            switch (_distr.toLowerCase()) {
                case "uniform":
                    settings.initDistribution = InitDistribution.UNIFORM;
                    break;
                case "constant":
                    settings.initDistribution = InitDistribution.CONSTANT;
                    break;
                case "normal":
                    settings.initDistribution = InitDistribution.NORMAL;
                    break;
                case "longtail":
                    settings.initDistribution = InitDistribution.LONGTAIL;
                    break;
                default:
                    LOG.severe("unrecognized distribution: " + _distr);
            }
        }

        if (cmd.hasOption("initialization")) {
            String _weightInit = cmd.getOptionValue("initialization");
            switch (_weightInit.toLowerCase()) {
                case "simple":
                    settings.initializer = InitSet.SIMPLE;
                    break;
                case "glorot":
                    settings.initializer = InitSet.GLOROT;
                    break;
                case "he":
                    settings.initializer = InitSet.HE;
                    break;
                default:
                    LOG.severe("unrecognized initialization: " + _weightInit);
            }
        }

        if (cmd.hasOption("optimizer")) {
            String _optimizer = cmd.getOptionValue("optimizer", String.valueOf(getOptimizer()));
            switch (_optimizer.toLowerCase()) {
                case "sgd":
                    settings.setOptimizer(OptimizerSet.SGD);
                    break;
                case "adam":
                    settings.setOptimizer(OptimizerSet.ADAM);
                    break;
                default:
                    LOG.severe("unrecognized optimizer: " + _optimizer);
            }
        }

        if (cmd.hasOption("learningRate")) {
            String _learningRate = cmd.getOptionValue("learningRate", String.valueOf(initLearningRate));
            settings.initLearningRate = Double.parseDouble(_learningRate);
        }

        if (cmd.hasOption("learnRateDecay")) {
            String _decay = cmd.getOptionValue("learnRateDecay");
            settings.learnRateDecay = Double.parseDouble(_decay);
            if (settings.learnRateDecay > 0) {
                settings.islearnRateDecay = true;
            } else {
                settings.islearnRateDecay = false;
            }
        }

        if (cmd.hasOption("decaySteps")) {
            String _decay = cmd.getOptionValue("decaySteps");
            settings.decaySteps = Integer.parseInt(_decay);
        }

        if (cmd.hasOption("trainingSteps")) {
            String _trainingSteps = cmd.getOptionValue("trainingSteps", String.valueOf(maxCumEpochCount));
            settings.maxCumEpochCount = Integer.parseInt(_trainingSteps);
        }

        if (cmd.hasOption("recalculationEpocha")) {
            String _limit = cmd.getOptionValue("recalculationEpocha");
            settings.resultsRecalculationEpochae = Integer.parseInt(_limit);
        }

        if (cmd.hasOption("evaluationMode")) {
            String _evaluationMode = cmd.getOptionValue("evaluationMode", "classification");
            switch (_evaluationMode) {
                case "classification":
                    settings.trainOnlineResultsType = ResultsType.CLASSIFICATION;
                    settings.trainRecalculationResultsType = ResultsType.DETAILEDCLASSIFICATION;
                    settings.validationResultsType = ResultsType.DETAILEDCLASSIFICATION;
                    settings.testResultsType = ResultsType.DETAILEDCLASSIFICATION;
                    break;
                case "regression":
                    settings.trainOnlineResultsType = ResultsType.REGRESSION;
                    settings.trainRecalculationResultsType = ResultsType.REGRESSION;
                    settings.validationResultsType = ResultsType.REGRESSION;
                    settings.testResultsType = ResultsType.REGRESSION;
                    break;
                case "kbc":
                    settings.trainOnlineResultsType = ResultsType.CLASSIFICATION;
                    settings.trainRecalculationResultsType = ResultsType.KBC;
                    settings.validationResultsType = ResultsType.KBC;
                    settings.testResultsType = ResultsType.KBC;
                    settings.groundingMode = GroundingMode.GLOBAL;
            }
        }

        if (cmd.hasOption("errorFunction")) {
            String _errorFunction = cmd.getOptionValue("errorFunction", "MSE");
            switch (_errorFunction.toLowerCase()) {
                case "mse":
                    settings.errorFunction = ErrorFcn.SQUARED_DIFF;
                    settings.errorAggregationFcn = CombinationFcn.AVG;
                    break;
                case "xent":
                    settings.errorFunction = ErrorFcn.CROSSENTROPY;
                    settings.errorAggregationFcn = CombinationFcn.AVG;
                    break;
            }
        }

        if (cmd.hasOption("atomCombination")) {
            String _fnc = cmd.getOptionValue("atomCombination");
            settings.atomNeuronCombination = parseCombination(_fnc);
        }
        if (cmd.hasOption("atomTransformation")) {
            String _fnc = cmd.getOptionValue("atomTransformation");
            settings.atomNeuronTransformation = parseTransformation(_fnc);
        }
        if (cmd.hasOption("ruleCombination")) {
            String _fnc = cmd.getOptionValue("ruleCombination");
            settings.ruleNeuronCombination = parseCombination(_fnc);
        }
        if (cmd.hasOption("ruleTransformation")) {
            String _fnc = cmd.getOptionValue("ruleTransformation");
            settings.ruleNeuronTransformation = parseTransformation(_fnc);
        }
        if (cmd.hasOption("aggFunction")) {
            String _fnc = cmd.getOptionValue("aggFunction");
            settings.aggNeuronAggregation = parseCombination(_fnc);
        }

        if (cmd.hasOption("sourcesDir")) {
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

        if (cmd.hasOption("debug") || settings.debugAll) {
            String _debug = cmd.getOptionValue("debug", "all");
            settings.drawing = true;
            settings.mainMode = MainMode.DEBUGGING;
            switch (_debug) {
                case "all":
                    settings.maxCumEpochCount = 2;
                    settings.resultsRecalculationEpochae = 2;
                    settings.debugAll = true;
                    settings.intermediateDebug = true;
                    settings.debugPipeline = true;
                    settings.debugTemplate = true;
                    settings.debugGrounding = true;
                    settings.debugNeuralization = true;
                    settings.debugSampleTraining = true;
                    settings.debugTemplateTraining = true;
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
            settings.structuralIsoCompression = Integer.parseInt(_losslessCompression) > 0;
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
    public Boolean validate(StringBuilder message) {
        boolean valid = true;

        if (groundingMode == GroundingMode.SEQUENTIAL) {
            if (parallelGrounding)
                valid = false;
            message.append("Not possible");
        }
        if (!oneQueryPerExample) {
            if (explicitSupervisedGroundTemplatePruning)
                valid = false;
        }

        if (stratification && trainRecalculationResultsType == ResultsType.REGRESSION) {
            message.append("stratification not possible with regression");
            valid = false;
        }

        if (isoValueCompression && (atomNeuronTransformation == TransformationFcn.RELU || ruleNeuronTransformation == TransformationFcn.RELU)) {
            message.append("lossless network compression does not work together with ReLu activations functions.\n Either turn off the isovaluecompression or change activation function(s).");
            valid = false;
        }

        if (isoValueCompression && (aggNeuronAggregation == CombinationFcn.MAX || atomNeuronCombination == CombinationFcn.MAX || ruleNeuronCombination == CombinationFcn.MAX)) {
            message.append("lossless network compression does not work well with MAX aggregation function.\n Either turn off the isovaluecompression or change activation function(s).");
            valid = false;
        }

        //todo more validation and inference of settings

        return valid;
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
            neuralNetsSupervisedPruning = false;
        }
        if (groundingMode == GroundingMode.GLOBAL) {
            possibleNeuronSharing = true;
            neuralNetsSupervisedPruning = false;
        }

        //in case the outDir changed...
        resultFile = outDir + "/results";
        settingsExportFile = outDir + "/settings";
        sourcesExportFile = outDir + "/sources";
        console = outDir + "/consoleOutput";
        exportDir = outDir + "/export";

        if (chainPruning || isoValueCompression || neuralNetsSupervisedPruning || copyOutInputOvermapping || isoGradientCompression || mergeIdenticalWeightedInputs || removeIdenticalUnweightedInputs || cycleBreaking || collapseWeights || expandEmbeddings)
            neuralNetsPostProcessing = true;
        else
            neuralNetsPostProcessing = false;

//
//        if (getOptimizer() == OptimizerSet.ADAM) {
//            initLearningRate = 0.01;
//        } else if (getOptimizer() == OptimizerSet.SGD) {
//            initLearningRate = 0.3;
//        }

        if (validationResultsType == ResultsType.DETAILEDCLASSIFICATION || validationResultsType == ResultsType.KBC) {
            calculateBestThreshold = true;  //it does not cost more then
        }

        if (debugAll || debugNeuralization || debugTemplateTraining || debugSampleTraining) {
            inferOutputFcns = false;
        }

        if (inferOutputFcns) {
            if (trainOnlineResultsType == ResultsType.REGRESSION) {
                errorFunction = ErrorFcn.SQUARED_DIFF;
            } else {
                if (squishLastLayer) {
                    errorFunction = ErrorFcn.SOFTENTROPY;
                } else {
                    errorFunction = ErrorFcn.CROSSENTROPY;
                }
            }
        }
        if (errorFunction == ErrorFcn.SOFTENTROPY) {
            squishLastLayer = true;
        }

//        resultsRecalculationEpochae = maxCumEpochCount / 100;
        //todo rest

        finish();
    }

    /**
     * Steps to be performed once the settings are totally complete, i.e. after all the inference and validation
     */
    private void finish() {

    }

    public Settings updateFromJson(Path inPath) {
        InstanceCreator<Settings> creator = type -> Settings.this;
        Gson gson = new GsonBuilder().registerTypeAdapter(Settings.class, creator).create();
        try {
            String json = new String(Files.readAllBytes(inPath));
            Settings settings = gson.fromJson(json, Settings.class);
            return settings;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Settings loadFromJson(Path inPath) {
        Settings settings = new Settings().updateFromJson(inPath);
        return settings;
    }

    public String export() {
        if (exportType == ExportFileType.JSON)
            return exportToJson();
        else {
            LOG.warning("Only exporting of Settings to JSON is supported");
            return exportToJson();
        }
    }

    public String exportToJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeSpecialFloatingPointValues()
                .create();
        String json = gson.toJson(this);
        return json;
    }
}
