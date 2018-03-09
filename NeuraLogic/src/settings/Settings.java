package settings;

import constructs.template.transforming.TemplateReducing;
import ida.utils.tuples.Pair;
import neuralogic.ParseTreeProcessor;
import parsing.alternatives.PlainTextExampleParser;
import parsing.alternatives.PlainTextQueryParser;
import utils.Utilities;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.UnknownFormatFlagsException;

/**
 * Created by gusta on 8.3.17.
 */
public class Settings {
    NumberFormat nf = new DecimalFormat("#.##########");

    //public SourceFiles sf;

    public TemplateReducing templateReducer;
    public boolean reduceTemplate;
    public String templatePath;

    private Settings() {
        reduceTemplate = true;
    }

    public static Settings constructFrom(Map<String, String> params) throws IOException {
        Settings settings = new Settings();
        //TODO fill all the settings
        if (settings.sf.examplesPath.toFile().exists())
            throw new MissingResourceException("Need some examples to learn!", "Example", "");

        if (Utilities.identifyFileTypeUsingFilesProbeContentType(settings.sf.templatePath.toString()).equals("text/plain")) {
            //TODO add more detailed scan of the file to recognize what parser will be appropriate
            settings.sf.tp = new ParseTreeProcessor();
        } else {
            throw new UnknownFormatFlagsException("File type of input template/rules not recognized!");
        }
        if (Utilities.identifyFileTypeUsingFilesProbeContentType(settings.sf.examplesPath.toString()).equals("text/plain")) {
            settings.sf.ep = new PlainTextExampleParser();
        } else {
            throw new UnknownFormatFlagsException("File type of input examples not recognized!");
        }
        if (Utilities.identifyFileTypeUsingFilesProbeContentType(settings.sf.queriesPath.toString()).equals("text/plain")) {
            settings.sf.qp = new PlainTextQueryParser();
        } else {
            throw new UnknownFormatFlagsException("File type of input queries not recognized!");
        }

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


    public Pair<Boolean, String> validate() {
        return null;
    }
}
