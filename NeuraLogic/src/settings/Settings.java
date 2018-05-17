package settings;

import constructs.template.transforming.TemplateReducing;
import grounding.Grounder;
import grounding.bottomUp.BottomUp;
import grounding.topDown.TopDown;
import ida.utils.tuples.Pair;
import org.apache.commons.cli.CommandLine;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    public Grounder grounder;
    /**
     * Type of grounder
     */
    public groundingAlgo grounding = groundingAlgo.BUP;
    public enum groundingAlgo {
        BUP, TDOWN
    }

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


    //----------------Template Transformations
    public boolean reduceTemplate = true;
    public TemplateReducing templateReducer;



    //----------------INFERRED SETTINGS
    public boolean structureLearning;
    public boolean testFileProvided;
    public boolean training;

    public boolean foldFiles;
    public boolean crossvalidation;
    public boolean stratification = true;


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

        Pair<Boolean, String> sv = sources.validate(this);
        valid &= sv.r;
        message.append(sv.s);

        //TODO more validation and inference of settings

        return new Pair(valid, message);
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
