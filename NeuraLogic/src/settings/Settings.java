package settings;

import ida.utils.tuples.Pair;
import org.apache.commons.cli.CommandLine;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Settings {
    private static final Logger LOG = Logger.getLogger(Settings.class.getName());

    //------------------Global structures
    /**
     * Global random generator
     */
    Random random;
    public int seed = 1;
    /**
     * Global number format for all printing
     */
    NumberFormat nf = new DecimalFormat("#.##########");

    //------------------Grounding
    /**
     * Ground train+test example sets TOGETHER (with the same cache)
     */
    public boolean trainTestJointGrounding; //TODO implement this
    /**
     * Ground networks (in a given Grounder context) may share common parts (i.e. Grounder has a cache)
     */
    public boolean globallySharedGroundings;
    /**
     * Ground networks are grounded in a specific given sequence (i.e. sharing only with previous examples)
     */
    public boolean sequentiallySharedGroundings;
    /**
     * If there's no need for keeping the given sequence, ground in parallel in the given context
     */
    public boolean parallelGrounding = !sequentiallySharedGroundings;

    /**
     * Type of grounder
     */
    public groundingAlgo grounding = groundingAlgo.BUP;

    public enum groundingAlgo {
        BUP, TDOWN, GRINGO
    }

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
    public boolean neuralNetsPostProcessing;
    /**
     * Copy out neurons fully instead of input over-mappings
     */
    public boolean removeInputOvermapping;
    /**
     * Remove recurrent edges from the neural networks
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
     * If there are embedding constructs in the Template, expand them (copy-multiple neurons) after Network creation
     */
    public boolean expandEmbeddings;

    //-----------------Structure Learning
    public boolean structureLearning;

    //-----------------Source files
    /**
     * Are the data sources files? (not e.g. streams from memory/internet)
     */
    public boolean sourceFiles = true;
    public String sourcePath = ".";
    public String templateFile = "template.txt";
    public String trainExamplesFile = "trainExamples.txt";
    public String testExamplesFile = "testExamples.txt";
    public String trainQueriesFile = "trainQueries.txt";
    public String testQueriesFile = "testQueries.txt";

    public String foldsPrefix = "fold";

    //----------------Crossvaldiation

    public int foldsCount = 5;
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
     * If the template contains facts, infer all other possible true facts as a preprocessing step (to save time inferring the same things over and over later)
     */
    public boolean inferTemplateFacts = true;
    /**
     * In advance of grounding (theorem proving), remove rules that are irrelevant to the given query (with no chance to be in support)
     */
    public boolean supervisedTemplateGraphPruning = true;   //todo measure if this actually helps

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
     * TODO Setup globally default settings here
     */
    public Settings() {


    }

    public void setupFromCommandline(CommandLine cmd) {

        String _seed = cmd.getOptionValue("seed", String.valueOf(seed));
        random = new Random(Integer.parseInt(_seed));
        //TODO fill all the settings

    }


    /**
     * Check for banned combinations here
     * @return
     */
    public Pair<Boolean, String> validate() {
        boolean valid = true;
        StringBuilder message = new StringBuilder();

        if (sequentiallySharedGroundings) {
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
    public void infer(){
        if (reduceTemplate) graphTemplate = true;
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
