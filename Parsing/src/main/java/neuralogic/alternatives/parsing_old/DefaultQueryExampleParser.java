package neuralogic.alternatives.parsing_old;

import learning.Example;
import learning.Query;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Stream;

/**
 * If the trainQueries are not explicitly specified but rather each example itself is labeled
 * Created by gusta on 26.3.17.
 */
public class DefaultQueryExampleParser implements QueryParser,ExampleParser {
    @Override
    public boolean isValid(String input) {
        return false;
    }

    @Override
    public Stream<Example> parseExamples(Reader reader) {
        return null;
    }

    @Override
    public Stream<List<Query>> parseQueries(Reader reader) throws IOException {
        return null;
    }
}
