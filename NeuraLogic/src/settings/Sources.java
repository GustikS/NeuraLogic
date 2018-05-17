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
public abstract class Sources {

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


    public abstract Pair<Boolean, String> validate(Settings settings);

    public static Sources setupFromCommandline(Settings settings, CommandLine cmd) {
        Sources sources;
        if (settings.sourceFiles) {
            sources = new SourceFiles(settings, cmd);
        } else {
            LOG.severe("Input streams other than from source files not implemented yet");
            throw new NotImplementedException();
        }
        sources.validate(settings);
        return sources;
    }
}