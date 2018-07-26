package settings;

import ida.utils.tuples.Pair;
import neuralogic.examples.PlainExamplesParseTree;
import neuralogic.queries.PlainQueriesParseTree;
import neuralogic.template.PlainTemplateParseTree;
import org.apache.commons.cli.CommandLine;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;

/**
 * Container for data sources of trainExamples, trainQueries and template
 * <p>
 * Created by gusta on 26.3.17.
 */
public abstract class Sources { //TODO split this class to carry 2 separate Source objects (train a test) (?) - do next

    private static final Logger LOG = Logger.getLogger(Sources.class.getName());

    /**
     * Source files may be further (even recursively) split into folds
     * TODO - test this behavior
     */
    public List<Sources> folds;
    protected Sources parent;

    //TODO change to correct abstract/specific parse trees for each type
    public PlainTemplateParseTree templateParseTree;
    public PlainExamplesParseTree trainExamplesParseTree;
    public PlainExamplesParseTree testExamplesParseTree;
    public PlainQueriesParseTree trainQueriesParseTree;
    public PlainQueriesParseTree testQueriesParseTree;

    public Reader templateReader;
    public Reader trainExamplesReader;
    public Reader testExamplesReader;
    public Reader trainQueriesReader;  // these should always be present for learning
    public Reader testQueriesReader;


    //----------------INFERRED SETTINGS
    public boolean templateProvided;

    public boolean trainExamplesSeparate;
    public boolean testExamplesSeparate;
    public boolean trainQueriesSeparate;
    public boolean testQueriesSeparate;

    public boolean trainExamplesProvided;
    public boolean trainQueriesProvided;
    public boolean testExamplesProvided;
    public boolean testQueriesProvided;

    public boolean trainQueriesLinkedById;
    public boolean testQueriesLinkedById;

    public boolean foldFiles;   //i.e. external x-val files

    //-----------------Learning modes
    public boolean crossvalidation = true;
    public boolean trainTest = false;
    public boolean trainOnly = false;
    public boolean testOnly = false;


    public abstract Pair<Boolean, String> validate(Settings settings);

    public void infer(Settings settings) {
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
            if (templateParseTree.getRoot().templateLine() == null)
                templateProvided = false;
            templateProvided = true;
        }

        trainExamplesSeparate = trainExamplesReader == null ? false : !trainExamplesParseTree.isEmpty();
        testExamplesSeparate = testExamplesReader != null ? !testExamplesParseTree.isEmpty() : false;
        trainQueriesSeparate = trainQueriesReader == null ? false : !trainQueriesParseTree.isEmpty();
        testQueriesSeparate = testQueriesReader != null ? !testQueriesParseTree.isEmpty() : false;

        if (trainExamplesSeparate) {
            trainExamplesProvided = true;
            if (trainExamplesParseTree.getRoot().label() != null) {
                trainQueriesProvided = true;
            }
        } else {
            trainExamplesProvided = false;
        }

        if (testExamplesSeparate) {
            testExamplesProvided = true;
            if (testExamplesParseTree.getRoot().label() != null) {
                testQueriesProvided = true;
            }
        } else {
            testExamplesProvided = false;
        }
        if (trainQueriesSeparate) {
            trainQueriesProvided = true;
            if (trainQueriesParseTree.getRoot().atom() != null) {
                trainQueriesLinkedById = true;
            }
        }
        if (testQueriesSeparate) {
            testQueriesProvided = true;
            if (testQueriesParseTree.getRoot().atom() != null) {
                testQueriesLinkedById = true;
            }
        }

        /*
        if (testQueriesProvided) {
            settings.crossvalidation = false;
            LOG.info("Turning off crossvalidation because test queries are provided.");
        }
        */

        if (!crossvalidation) {
            if (trainQueriesProvided && testQueriesProvided) {
                trainTest = true;
            } else if (trainQueriesProvided) {
                trainOnly = true;
            } else if (testQueriesProvided) {
                if (!templateProvided) {
                    LOG.warning("Incosistent learning mode inference for this Source (missing template).");
                }
                testOnly = true;
            } else {
                LOG.warning("Incosistent learning mode inference for this Source.");
            }
        }
    }

    /**
     * Validation
     *
     * @param settings
     * @return
     */
    public Pair<Boolean, String> isValid(Settings settings) {
        boolean valid = true;
        String msg = "";
        infer(settings);
        //TODO add some general validation
        return validate(settings);
    }


    public static Sources setupFromCommandline(Settings settings, CommandLine cmd) {
        Sources sources;
        if (settings.sourceFiles) {
            sources = new SourceFiles(settings, cmd);
        } else {
            LOG.severe("Input streams other than from source files not implemented yet");
            throw new NotImplementedException();
        }
        return sources;
    }
}