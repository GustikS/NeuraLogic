package settings;

import ida.utils.tuples.Pair;
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
public class Sources {

    private static final Logger LOG = Logger.getLogger(Sources.class.getName());

    /**
     * Source files may be further (even recursively) split into folds
     * TODO - test this behavior
     */
    public List<Sources> folds;
    protected Sources parent;

    public Source train;
    public Source test;

    //TODO change to correct abstract/specific parse trees for each type
    public PlainTemplateParseTree templateParseTree;
    //TODO put Readers down in hierarchy into files? (maybe not)
    public Reader templateReader;

    //----------------INFERRED SETTINGS
    public boolean templateProvided;
    public boolean foldFiles;   //i.e. external x-val files

    //-----------------Learning modes
    public boolean crossvalidation = true;
    public boolean trainTest = false;
    public boolean trainOnly = false;
    public boolean testOnly = false;

    public Sources(Settings settings) {
        train = new Source();
        test = new Source();
    }

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
    public Pair<Boolean, String> validate(Settings settings) {
        boolean valid = true;
        String msg = "";

        infer(settings);

        if (train.QueriesReader == null && test.QueriesReader == null && folds == null) {
            LOG.severe(msg += "Invalid learning setup - no trainQueriesSeparate nor testing samples provided");
            valid = false;
        }
        if (templateReader == null && train.QueriesReader == null && test.QueriesReader == null) {
            LOG.severe(msg += "Invalid learning setup - no template nor trainQueriesSeparate or testing samples provided");
            valid = false;
        }
        if (crossvalidation && (testOnly || trainTest || trainOnly)){
            LOG.severe(msg += "Invalid learning setup - cannot decide between crossvalidation and other modes.");
            valid = false;
        }
        if (foldFiles){
            Pair<Boolean, String> val = checkJointConsistency(folds);
        }
        //TODO add some general validation
        Pair<Boolean, String> valtrain = train.validate(settings);
        valid &= valtrain.r;
        msg += valtrain.s;
        Pair<Boolean, String> valtest = test.validate(settings);
        msg += valtest.s;
        valid &= valtest.r;
        return new Pair<>(valid, msg);
    }

    /**
     * All folds must have the same contents (wr.t. train test Source)
     * i.e. if one of them includes train, all must do, otherwise it is inconsistent
     * @param folds
     * @return
     */
    private Pair<Boolean, String> checkJointConsistency(List<Sources> folds) {
        //1) all folds have different templates is OK, but some have and some not is not ok, none have is ok, only superfold has template is ok
        return null;
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