package parsing.alternatives;

import constructs.example.AtomQuery;
import constructs.example.CompositeQuery;
import learning.Query;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * Created by gusta on 14.3.17.
 */
public class PlainTextQueryParser implements QueryParser {
    String querySeprator = ";";

    @Override
    public boolean isValid(String input) {
        return false;
    }

    @Override
    public Stream<List<Query>> parseQueries(Reader reader) {
        Stream<List<Query>> stream = new BufferedReader(reader).lines().map(line -> ParseQueryLine(line));
        return stream;
    }

    private List<Query> ParseQueryLine(String line) {
        List<Query> queries = new ArrayList<>(1);
        String[] split = line.split(";");
        for (String s : split) {
            Query q = parseQuery(s);
            queries.add(q);
        }
        return queries;
    }

    private Query parseQuery(String s) {
        if (s.contains(":-")){
            return CompositeQuery.parse(s);
        } else {
            return AtomQuery.parse(s);
        }
    }

}