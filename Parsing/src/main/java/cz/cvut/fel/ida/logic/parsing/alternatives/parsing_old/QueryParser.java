package cz.cvut.fel.ida.logic.parsing.alternatives.parsing_old;

import cz.cvut.fel.ida.learning.Query;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by gusta on 14.3.17.
 */
public interface QueryParser {

    boolean isValid(String input);

    public abstract Stream<List<Query>> parseQueries(Reader reader) throws IOException;
}
