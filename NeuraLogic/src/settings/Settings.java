package settings;

import org.apache.commons.cli.CommandLine;
import pipelines.Pipeline;
import utils.Exporter;
import utils.generic.Pair;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Settings {
    private static final Logger LOG = Logger.getLogger(Settings.class.getName());
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
    public static boolean supressLogFileOutput = false;
    /**
     * How detailed the log should be in general
     */
    public static Level loggingLevel = Level.FINEST;
    /**
     * Colorful console output?
     */
    public static boolean customLogColors = true;

    public static String logFile = "./out/Logging";

    //------------------Exporting

    public Exporter exporter;

    public String resultFile = "./out/results";

    public String tmpFile = "./out/tmpFile";

    public String console = "./out/consoleOutput";

    //------------------High level

    public Optimize optimize = Optimize.SPEED;

    public enum Optimize {
        MEMORY, SPEED, TRADEOFF
    }

    //------------------Global structures
    /**
     * Global random generator
     */
    public Random random;
    public int seed = 1;
    /**
     * Global number format for all printing
     */
    public static NumberFormat nf = new DecimalFormat("#.#####");

    //------------------Abstract Pipelines

    /**
     * A root pipeline of the actual program flow.
     */
    public Pipeline root;

    /**
     * Some major settings that influence pipelines creation have been changed on the run,
     * implying the need for rebuilding of every pipeline when entered (accept) during run
     */
    public boolean rebuildPipelines = false;

    /**
     * Limiting the input Sample stream to the first N samples. N <= 0 for no limit
     */
    public int limitSamples = 0;

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

    public double defaultConjunctWeight = 1.0;  //todo actually use these in some factory method
    public double defaultDisjunctWeight = 1.0;
    public double defaultConjunctionOffset = 1.0;
    public double defaultDisjunctionOffset = 1.0;

    /**
     * There is no actual weighting, just pooling, so identity weight
     */
    public double aggNeuronInputWeight = 1.0;
    /**
     * A whole pipeline of all postprocessing steps
     */
    public boolean neuralNetsPostProcessing = false;
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
    public boolean pruneNetworks;
    /**
     * Bottom-up value based sub-graph isomorphism collapsing (merging)
     */
    public boolean isoValueCompression;
    /**
     * Top-down value (gradient) based sub-graph isomorphism collapsing (merging)
     */
    public boolean isoGradientCompression;
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
    public int isoValuePrecision = 2;

    //-----------------Evaluation & Training

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
    public int maxCumEpochCount = 20;

    /**
     * Shuffle samples before neural training (only turn off for debugging purposes)
     */
    public boolean shuffleBeforeTraining = true;

    /**
     * with Minibatch = Shuffle samples with each epoch to create different minibatches.
     * with SGD = Shuffle samples with each epoch to pass the samples in different orders (=shuffle even for the SGD mode)
     */
    public boolean shuffleEachEpoch;

    public boolean islearnRateDecay = false;

    public double initLearningRate = 0.1;

    public double dropoutRate = 0.0;

    public OptimizerSet optimizer = OptimizerSet.SGD;

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

    public ErrorFcn errorFunction = ErrorFcn.SQUARED_DIFF;

    public AggregationFcn errorAggregationFcn = AggregationFcn.AVG;

    public ActivationFcn atomNeuronActivation = ActivationFcn.SIGMOID;
    public AggregationFcn aggNeuronActivation = AggregationFcn.AVG;
    public ActivationFcn ruleNeuronActivation = ActivationFcn.SIGMOID;
    public ActivationFcn negation = ActivationFcn.REVERSE;

    public enum ActivationFcn {
        SIGMOID, LUKASIEWICZ, RELU, TANH, IDENTITY, REVERSE
    }

    public enum ErrorFcn {
        SQUARED_DIFF, ABS_DIFF
    }

    public enum AggregationFcn {
        AVG, MAX, MIN, SUM
    }

    public IterationMode iterationMode = IterationMode.Topologic;

    public enum IterationMode {
        Topologic, DFS_RECURSIVE, DFS_STACK, BFS
    }

    /**
     * Percentage of samples from train-set used for validation, 0 = empty validation set
     */
    public double trainValidationPercentage = 0;

    //-----------------Structure Learning
    public boolean structureLearning;

    //-----------------Source files
    /**
     * Format of the input (file) readers is for plain-text parsers (and not e.g. xml)
     */
    public boolean plaintextInput;
    /**
     * Are the data sources files? (not e.g. streams from memory/internet)
     */
    public boolean sourceFiles = true;

    public String sourcePath = ".";
    public String  templateFile = "template.txt";
    public String trainExamplesFile = "trainExamples.txt";
    public String testExamplesFile = "testExamples.txt";
    public String trainQueriesFile = "trainQueries.txt";
    public String testQueriesFile = "testQueries.txt";

    public String foldsPrefix = "fold";

    public String queryExampleSeparator = ":-";

    //----------------Crossvaldiation

    public int foldsCount = 5;
    public boolean stratification = false;
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
    public boolean oneQueryPerExample;
    /**
     * Queries and Examples are 1-1 and also ordered correspondingly (allows to just merge the 2 streams without terminating them)
     */
    public boolean queriesAlignedWithExamples;

    public double defaultSampleImportance = 1.0;
    public String sampleIdPrefix = "s";
    public String queriesBatchPrefix = "_b";


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
        exporter = new Exporter(this);
    }

    public void setupFromCommandline(CommandLine cmd) {

        String _seed = cmd.getOptionValue("seed", String.valueOf(seed));
        random = new Random(Integer.parseInt(_seed));

        String _groundingAlgorithm = cmd.getOptionValue("groundingAlgorithm", grounding.name());
        switch (_groundingAlgorithm) {
            case "BUp":
                grounding = GroundingAlgo.BUP;
                break;
            case "TDown":
                grounding = GroundingAlgo.TDOWN;
                break;
            case "Gringo":
                grounding = GroundingAlgo.GRINGO;
                break;
        }

        String _groundingMode = cmd.getOptionValue("groundingMode", "normal");
        switch (_groundingMode) {
            case "normal":
                groundingMode = GroundingMode.STANDARD;
                break;
            case "sequential":
                groundingMode = GroundingMode.SEQUENTIAL;
                break;
            case "global":
                groundingMode = GroundingMode.GLOBAL;
                break;
        }

        String _trainingSteps = cmd.getOptionValue("trainingSteps", String.valueOf(maxCumEpochCount));
        maxCumEpochCount = Integer.parseInt(_trainingSteps);

        String _evaluationMode = cmd.getOptionValue("evaluationMode", "classification");
        switch (_evaluationMode){
            case "classification" :
                regression = false;
                break;
            case "regression":
                regression = true;
                break;
        }

        String _errorFunction = cmd.getOptionValue("errorFunction", "MSE");
        switch (_errorFunction){
            case "MSE" :
                errorFunction = ErrorFcn.SQUARED_DIFF;
                errorAggregationFcn = AggregationFcn.AVG;
                break;
            case "XEnt":
                LOG.severe("XEnt not yet implemented");
                break;
        }

        //todo fill all the settings
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
        //TODO more validation and inference of settings

        return new Pair(valid, message);
    }

    /**
     * Infer all remaining settings from the given
     */
    public void infer() {
        if (reduceTemplate) graphTemplate = true;
        parentCounting = (iterationMode != IterationMode.Topologic);

        if (dropoutRate == 0 && !parentCounting) {
            neuralState = NeuralState.STANDARD;
        } else if (dropoutRate == 0 && parentCounting) {
            neuralState = NeuralState.PARENTS;
        } else if (dropoutRate > 0 && parentCounting) {
            neuralState = NeuralState.PAR_DROPOUT;
        }

        if (groundingMode == GroundingMode.SEQUENTIAL){
            forceFullNetworks = true;   //if we sequentially add new facts/rules, and then after grounding we take just the diff, the rules might not be connected, i.e. we need to turn them all blindly to neurons.
        }

        //TODO
    }

    public void importFromCSV(String inPath) {

    }

    public void importFromJson(String inPath) {


    }

    public void exportToCSV(String outPath) {

    }

    public void exportToJson(String outPath) {

    }
}
