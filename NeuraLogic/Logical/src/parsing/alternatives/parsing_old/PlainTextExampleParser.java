package parsing.alternatives.parsing_old;

import learning.Example;

import java.io.Reader;
import java.util.stream.Stream;

/**
 * Created by gusta on 14.3.17.
 */
public class PlainTextExampleParser implements ExampleParser {

    @Override
    public boolean isValid(String input) {
        return false;
    }

    @Override
    public Stream<Example> parseExamples(Reader reader) {
        return null;
    }
}