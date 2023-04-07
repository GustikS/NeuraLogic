package cz.cvut.fel.ida.setup;

import com.google.gson.*;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;

/**
 * Container for data sources of train/val/test Examples, train/val/test Queries and Template
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
    transient protected Sources parent;

    public Source train;
    public Source val;
    public Source test;

    private transient Reader templateReader;
    public boolean commonTemplate;

    //----------------INFERRED SETTINGS
    public boolean templateProvided;
    public transient FileInputStream binaryTemplateStream;

    public boolean foldFiles;   //i.e. external x-val files

    //-----------------Learning modes
    public boolean crossvalidation = false;
    public boolean trainTest = false;
    public boolean trainValTest = false;
    public boolean trainOnly = false;
    public boolean testOnly = false;
    public boolean drawing = false; // only debug

    public static Sources getSources(CommandLine cmd, Settings settings) throws Exception {
        Sources sources = null;
        try {
            settings = settings.setupFromCommandline(cmd);
            LOG.info("Settings loaded and set up.");
            sources = Sources.setupFromCommandline(settings, cmd);
            LOG.info("Sources loaded and set up.");
        } catch (Exception ex) {
            throw new Exception("Unable to parse Commandline arguments into settings/source files.\n" + ex);
//            System.exit(1);
        }

        settings.infer();   //  this is important here

        StringBuilder problems = new StringBuilder();
        Boolean validation = settings.validate(problems);
        if (!validation) {
            throw new Exception("Invalid pipelines setting.\n" + problems);
//            System.exit(2);
        }
        validation = sources.validate(settings, problems);
        if (!validation) {
            throw new Exception("Invalid source files configuration.\n" + problems);
//            System.exit(2);
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
        val = new Source();
        test = new Source();
    }

    public void infer(Settings settings) {
        if (folds != null && !folds.isEmpty()) {
            foldFiles = true;
            crossvalidation = true;
            settings.foldsCount = folds.size();
            for (Sources fold : folds) {
                fold.infer(settings);
            }
        } else {
            foldFiles = false;
        }

        if (getTemplateReader() == null && binaryTemplateStream == null) {
            templateProvided = false;
            settings.structureLearning = true;
        } else {
            templateProvided = true;
            if (folds != null && !folds.isEmpty()) {
                commonTemplate = true;
            }
        }

        train.infer(settings);
        val.infer(settings);
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
                if (val.QueriesProvided) {
                    trainValTest = true;
                }
            } else if (train.QueriesProvided) {
                trainOnly = true;
            } else if (test.QueriesProvided) {
                if (!templateProvided) {
                    LOG.warning("Incosistent learning mode inference for this Source (missing template).");
                }
                testOnly = true;
            } else if (settings.drawing) {
                drawing = true;
                settings.mainMode = Settings.MainMode.DEBUGGING;
                LOG.warning("Missing any learning queries (labels) - this pipeline will be useful for informative debugging/drawing only (no training possible)");
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

        if (!settings.allowStructureLearning && !templateProvided) {
            LOG.severe(msg = "Structure learning is forbidden (not implemented) but no template provided\n");
            problems.append(msg);
            valid = false;
        }

        if (!train.QueriesProvided && !test.QueriesProvided && (folds == null || folds.isEmpty())) {
            if (settings.drawing) {// missing queries -> might still be useful for drawing/debugging
                drawing = true;
                settings.mainMode = Settings.MainMode.DEBUGGING;
            } else {
                LOG.severe(msg = "Invalid learning setup - no training queries nor testing queries provided " + this.foldId + " \n");
                problems.append(msg);
                valid = false;
            }
        }
        if ((getTemplateReader() == null && binaryTemplateStream == null) && train.QueriesReader == null && test.QueriesReader == null) {
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
        boolean valid = true;
        for (int i = 1; i < folds.size(); i++) {
            if (folds.get(i).templateProvided != folds.get(i - 1).templateProvided) {
                problems.append("Some folds provide a template while others do not - this is ambiguous.");
                valid = false;
            }
        }
        for (Sources fold : folds) {
            valid &= fold.validate(settings, problems);
        }
        return valid;
    }


    public static Sources setupFromCommandline(Settings settings, CommandLine cmd) {
        Sources sources;
        if (settings.sourceFiles) {
            sources = new SourceFiles(settings, cmd);
        } else {
            throw new UnsupportedOperationException("Input streams other than from source files not implemented yet");
        }

        if (cmd.hasOption("xval") || settings.crossvalidation) {
            sources.crossvalidation = true;
        }
        return sources;
    }

    public String export() {
        if (settings.exportType == Settings.ExportFileType.JSON)
            return exportToJson();
        else {
            LOG.warning("Only exporting of Sources to JSON is supported");
            return exportToJson();
        }
    }

    public String exportToJson() {
        JsonSerializer<File> serializer = (file, type, jsonSerializationContext) -> {
            JsonObject jsonFile = new JsonObject();
            jsonFile.addProperty("path", file.getPath());
            return jsonFile;
        };

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(File.class, serializer)
                .serializeSpecialFloatingPointValues()
                .create();
        String json = gson.toJson(this);
        return json;
    }

    public Reader getTemplateReader() {
        return templateReader;
    }

    public void setTemplateReader(Reader templateReader) {
        this.templateReader = templateReader;
    }

    public Source getTrainOrTest() {
        if (testOnly) {
            return test;
        } else if (trainOnly) {
            return train;
        } else {
            throw new RuntimeException("Invalid setup of files in folds (ambiguous train vs. test)");
        }
    }
}