package settings;

import grounding.Grounder;
import grounding.bottomUp.BottomUp;
import grounding.topDown.TopDown;
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
    public Grounder grounder;   //TODO vyhodit, nechat v Settings jen primitivni typy
    public boolean onlineGrounding;

    /**
     * Type of grounder
     */
    public groundingAlgo grounding = groundingAlgo.BUP;

    public enum groundingAlgo {
        BUP, TDOWN
    }

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
    public boolean trainFoldsIsolation = false;

    //same template provided for all folds?
    public boolean commonTemplate;


    //----------------Template Transformations
    public boolean processMetadata = true;
    public boolean reduceTemplate = true;



    /**
     * TODO Setup globally default settings here
     */
    public Settings() {
    }

    public void setupFromCommandline(CommandLine cmd) {

        String _grounding = cmd.getOptionValue("grounding", grounding.name());
        switch (_grounding) {
            case "BUP":
                grounder = new BottomUp();
                break;
            case "TDOWN":
                grounder = new TopDown();
                break;
        }

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
