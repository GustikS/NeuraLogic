package settings;

import ida.utils.tuples.Pair;
import neuralogic.examples.PlainQueriesParseTree;
import neuralogic.grammarParsing.ParseTree;
import neuralogic.template.PlainTemplateParseTree;
import org.apache.commons.cli.CommandLine;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Reader;
import java.util.logging.Logger;

/**
 * Container for data sources of examples, queries and template
 * <p>
 * Created by gusta on 26.3.17.
 */
public abstract class Sources {

    private static final Logger LOG = Logger.getLogger(Sources.class.getName());

    //TODO change to correct abstract/specific parse trees for each type
    public PlainTemplateParseTree templateParseTree;
    public ParseTree examplesParseTree;
    public PlainQueriesParseTree trainQueriesParseTree;
    public ParseTree testQueriesParseTree;

    public Reader templateFileReader;
    public Reader examplesFileReader;
    public Reader trainQueriesFileReader;  // these should always be present for learning
    public Reader testQueriesFileReader;


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