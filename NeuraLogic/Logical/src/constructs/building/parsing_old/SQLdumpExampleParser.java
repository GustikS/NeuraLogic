package constructs.building.parsing_old;

import learning.Example;

import java.io.Reader;
import java.util.stream.Stream;

/**
 * Created by gusta on 16.3.17.
 */
public class SQLdumpExampleParser implements ExampleParser{
    @Override
    public boolean isValid(String input) {
        return false;
    }

    @Override
    public Stream<Example> parseExamples(Reader reader) {
        return null;
    }
}
