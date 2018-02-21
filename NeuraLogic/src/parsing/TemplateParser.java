package parsing;

import constructs.template.Template;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by gusta on 8.3.17.
 */
public interface TemplateParser {

    boolean isValid(String input);

    public abstract Template parseTemplate(Reader reader) throws IOException;

}