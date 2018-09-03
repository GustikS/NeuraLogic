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
    public boolean sharedGroundings;
    /**
     * Ground networks are grounded in a specific sequence (i.e. sharing only with previous examples)
     */
    public boolean sequentialGrounding;

    public boolean parallelGrounding = true;

    /**
     * Type of grounder
     */
    public groundingAlgo grounding = groundingAlgo.BUP;

    public enum groundingAlgo {
        BUP, TDOWN
    }

    //-----------------Neural nets creation

    /**
     * full unsupervised grounding, even if query is provided
     */
    public boolean forceFullNetworks;

    public double defaultConjunctWeight = 1.0;  //todo actually use these in some factory method
    public double defaultDisjunctWeight = 1.0;
    public double defaultConjunctionOffset = 1.0;
    public double defaultDisjunctionOffset = 1.0;

    /**
     * There is no actual weighting, just pooling, so identity weight
     */
    public double aggNeuronInputWeight = 1.0;

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
     * same template provided for all folds?
     */
    public boolean commonTemplate;

    //----------------Template Transformations

    /**
     * Apply all metadata read from sources
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

    //----------------Learning Samples

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


    public Pair<Boolean, String> validate() {
        boolean valid = true;
        StringBuilder message = new StringBuilder();


        //TODO more validation and inference of settings

        return new Pair(valid, message);
    }

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
