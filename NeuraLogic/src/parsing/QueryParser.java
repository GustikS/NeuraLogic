package parsing;

import learning.Query;

import java.io.Reader;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by gusta on 14.3.17.
 */
public interface QueryParser {

    boolean isValid(String input);

    public abstract Stream<List<Query>> parseQueries(Reader reader);
}
