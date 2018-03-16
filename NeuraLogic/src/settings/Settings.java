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

    /**
     * are the data sources files? (not e.g. streams from memory/internet)
     */
    public boolean sourceFiles = true;

    Random random;
    NumberFormat nf = new DecimalFormat("#.##########");


    public enum groundingAlgo {
        BUP, TDOWN
    }

    public groundingAlgo grounding = groundingAlgo.BUP;

    public int seed = 1;
    public String sourcePath = ".";
    public String foldsPrefix = "fold";
    public String testFile = "test.txt";
    public int folds = 5;
    public String queriesFile = "queries.txt";
    public String templateFile = "template.txt";
    public String examplesFile = "examples.txt";


    public TemplateReducing templateReducer;


    public Grounder grounder;

    public boolean structureLearning;
    public boolean testFileProvided;
    public boolean training;

    public boolean foldFiles;
    public boolean crossvalidation;
    public boolean stratification = true;

    public boolean reduceTemplate = true;

    /**
     * TODO Setup globally default settings here
     */
    public Settings() {
        reduceTemplate = true;

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
