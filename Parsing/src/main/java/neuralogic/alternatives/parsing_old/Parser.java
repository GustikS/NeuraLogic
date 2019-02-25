package neuralogic.alternatives.parsing_old;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public interface Parser<T> {

    default boolean isValid(String input) {
        try {
            parse(new StringReader(input));
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    default boolean isSameAs(String input, T output) {
        if (!isValid(input)) return false;
        try {
            return parse(new StringReader(input)).equals(output);
        } catch (Exception ex) {
            return false;
        }
    }

    T parse(Reader reader) throws IOException;
}