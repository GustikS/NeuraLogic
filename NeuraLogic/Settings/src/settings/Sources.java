package settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.CommandLine;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;

/**
 * Container for data sources of train/test Examples, train/test Queries and Template
 * <p>
 * Created by gusta on 26.3.17.
 */
public class Sources {

    private static final Logger LOG = Logger.getLogger(Sources.class.getName());

    protected String foldId = "";
    transient protected Settings settings;

    /**
     * Source files may be further (even recursively) split into folds
     * TODO - test this behavior
     */
    public List<Sources> folds;
    protected Sources parent;

    public Source train;
    public Source test;

    public transient Reader templateReader;

    //----------------INFERRED SETTINGS
    public boolean templateProvided;
    public boolean foldFiles;   //i.e. external x-val files

    //-----------------Learning modes
    public boolean crossvalidation = false;
    public boolean trainTest = false;
    public boolean trainOnly = false;
    public boolean testOnly = false;

    public static Sources getSources(CommandLine cmd, Settings settings) {
        Sources sources = null;
        try {
            settings = settings.setupFromCommandline(cmd);
            LOG.info("Settings loaded and set up.");
            sources = Sources.setupFromCommandline(settings, cmd);
            LOG.info("Sources loaded and set up.");
        } catch (Exception ex) {
            LOG.severe("Unable to parse Commandline arguments into settings/source files.\n" + ex);
            System.exit(1);
        }

        settings.infer();
        StringBuilder problems = new StringBuilder();
        Boolean validation = settings.validate(problems);
        if (!validation) {
            LOG.severe("Invalid pipelines setting.\n" + problems);
            System.exit(2);
        }
        validation = sources.validate(settings, problems);
        if (!validation) {
            LOG.severe("Invalid source files configuration.\n" + problems);
            System.exit(2);
        }
        return sources;
    }

    public Sources(String foldId, Settings settings) {
        this(settings);
        this.foldId = foldId;
    }

    public Sources(Settings settings) {
        this.settings = settings;
        train = new Source();
        test = new Source();
    }

    public void infer(Settings settings) {  //todo now export after this
        if (folds != null) {
            foldFiles = true;
            crossvalidation = true;
        } else {
            foldFiles = false;
        }

        if (templateReader == null) {
            templateProvided = false;
            settings.structureLearning = true;
        } else {
            templateProvided = true;
        }

        train.infer(settings);
        test.infer(settings);

        /*
        if (testQueriesProvided) {
            settings.crossvalidation = false;
            LOG.info("Turning off crossvalidation because test queries are provided.");
        }
        */

        if (!crossvalidation) {
            if (train.QueriesProvided && test.QueriesProvided) {
                trainTest = true;
            } else if (train.QueriesProvided) {
                trainOnly = true;
            } else if (test.QueriesProvided) {
                if (!templateProvided) {
                    LOG.warning("Incosistent learning mode inference for this Source (missing template).");
                }
                testOnly = true;
            } else {
                LOG.warning("Incosistent learning mode inferred for this Source.");
            }
        }

        if (templateProvided && train.QueriesProvided && !train.ExamplesProvided) {
            settings.groundingMode = Settings.GroundingMode.GLOBAL;
        }

        finish(settings);
    }

    /**
     * Steps to be performed once the settings are totally complete, i.e. after all the inference and validation
     */
    private void finish(Settings settings) {
        //todo
    }

    /**
     * Validation
     *
     * @param settings
     * @return
     */
    public Boolean validate(Settings settings, StringBuilder problems) {
        boolean valid = true;

        infer(settings);
        String msg;

        if (!settings.allowStructureLearning && !templateProvided){
            LOG.severe(msg = "Structure learning is forbidden (not implemented) but no template provided\n");
            problems.append(msg);
            valid = false;
        }

        if (!train.QueriesProvided && !test.QueriesProvided && folds == null) {
            LOG.severe(msg = "Invalid learning setup - no training queries nor testing queries provided\n");
            problems.append(msg);
            valid = false;
        }
        if (templateReader == null && train.QueriesReader == null && test.QueriesReader == null) {
            LOG.severe(msg = "Invalid learning setup - no template nor queries provided\n");
            problems.append(msg);
            valid = false;
        }
        if (crossvalidation && (testOnly || trainTest || trainOnly)) {
            LOG.severe(msg = "Invalid learning setup - cannot decide between crossvalidation and other modes.\n");
            problems.append(msg);
            valid = false;
        }
        if (foldFiles) {
            boolean val = checkJointConsistency(folds, problems);
            valid &= val;
        }
        //TODO add some general validation
        Boolean valtrain = train.validate(settings, problems);
        valid &= valtrain;
        Boolean valtest = test.validate(settings, problems);
        valid &= valtest;
        return valid;
    }

    /**
     * All folds must have the same contents (wr.t. train test Source)
     * i.e. if one of them includes train, all must do, otherwise it is inconsistent
     *
     * @param folds
     * @return
     */
    private boolean checkJointConsistency(List<Sources> folds, StringBuilder problems) {
        //1) all folds have different templates is OK, but some have and some not is not ok, none have is ok, only superfold has template is ok
        return true;
    }


    public static Sources setupFromCommandline(Settings settings, CommandLine cmd) {
        Sources sources;
        if (settings.sourceFiles) {
            sources = new SourceFiles(settings, cmd);
        } else {
            LOG.severe("Input streams other than from source files not implemented yet");
            throw new NotImplementedException();
        }

        if (cmd.hasOption("xval") || settings.crossvalidation) {
            sources.crossvalidation = true;
        }
        return sources;
    }

    public String export() {
        if (settings.blockExporting == Settings.BlockExporting.JSON)
            return exportToJson();
        else {
            LOG.warning("Only exporting of Sources to JSON is supported");
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