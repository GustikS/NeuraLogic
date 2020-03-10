package settings;

import java.io.Reader;
import java.util.logging.Logger;

public class Source {
    private static final Logger LOG = Logger.getLogger(Source.class.getName());

    public transient Reader ExamplesReader;
    public transient Reader QueriesReader;  // the queries should always be present for learning

    public boolean ExamplesSeparate;
    public boolean QueriesSeparate;

    public boolean ExamplesProvided;
    public boolean QueriesProvided;

    public boolean QueriesLinkedById;

    public Boolean validate(Settings settings, StringBuilder problems) {
        //TODO
        return true;
    }

    public void infer(Settings settings) {

        ExamplesSeparate = ExamplesReader == null ? false : true;
        QueriesSeparate = QueriesReader == null ? false : true;

        if (ExamplesSeparate) {
            ExamplesProvided = true;
        } else {
            ExamplesProvided = false;
        }

        if (QueriesSeparate) {
            QueriesProvided = true;
            if (ExamplesProvided)
                QueriesLinkedById = true;
        }
    }
}
