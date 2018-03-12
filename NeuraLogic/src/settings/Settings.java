package settings;

import constructs.template.transforming.TemplateReducing;
import grounding.Grounder;
import grounding.bottomUp.BottomUp;
import grounding.topDown.TopDown;
import ida.utils.tuples.Pair;
import org.apache.commons.cli.CommandLine;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * Created by gusta on 8.3.17.
 */
public class Settings {
    Random random;
    NumberFormat nf = new DecimalFormat("#.##########");

    DefaultSettings defaults = new DefaultSettings();

    public SourceFiles sourceFiles;

    public TemplateReducing templateReducer;
    public boolean reduceTemplate;

    String _grounding;
    public Grounder grounder;

    String _seed;
    public boolean structureLearning;
    public boolean testFile;
    public boolean training;
    public boolean foldFiles;

    /**
     * TODO Setup globally default settings here
     */
    public Settings() {
        reduceTemplate = true;
    }

    public void setupFromCommandline(CommandLine cmd) throws NumberFormatException, FileNotFoundException {

        sourceFiles = new SourceFiles(this, cmd);
        sourceFiles.validate(this);

        _grounding = cmd.getOptionValue("grounding", defaults.grounding);
        switch (_grounding) {
            case "up":
                grounder = new BottomUp();
                break;
            case "down":
                grounder = new TopDown();
                break;
        }

        _seed = cmd.getOptionValue("seed", defaults.seed);
        random = new Random(Integer.parseInt(_seed));
        //TODO fill all the settings

    }


    public Pair<Boolean, String> validate() {

        return null;
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
