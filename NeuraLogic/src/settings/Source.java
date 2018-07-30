package settings;

import ida.utils.tuples.Pair;
import neuralogic.examples.PlainExamplesParseTree;
import neuralogic.queries.PlainQueriesParseTree;

import java.io.Reader;
import java.util.logging.Logger;

public class Source {
    private static final Logger LOG = Logger.getLogger(Source.class.getName());

    public PlainExamplesParseTree ExamplesParseTree;
    public PlainQueriesParseTree QueriesParseTree;

    public Reader ExamplesReader;
    public Reader QueriesReader;  // these should always be present for learning

    public boolean ExamplesSeparate;
    public boolean QueriesSeparate;

    public boolean ExamplesProvided;
    public boolean QueriesProvided;

    public boolean QueriesLinkedById;

    public Pair<Boolean, String> validate(Settings settings){
        //TODO
        return null;
    }

    public void infer(Settings settings) {

        ExamplesSeparate = ExamplesReader == null ? false : !ExamplesParseTree.isEmpty();
        QueriesSeparate = QueriesReader == null ? false : !QueriesParseTree.isEmpty();

        if (ExamplesSeparate) {
            ExamplesProvided = true;
            if (ExamplesParseTree.getRoot().label() != null) {
                QueriesProvided = true;
            }
        } else {
            ExamplesProvided = false;
        }

        if (QueriesSeparate) {
            QueriesProvided = true;
            if (QueriesParseTree.getRoot().atom() != null) {
                QueriesLinkedById = true;
            }
        }
    }
}
