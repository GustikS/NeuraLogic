package constructs.building.parsing_old;

import constructs.template.Template;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by gusta on 8.3.17.
 */
public class JsonTemplateParser implements TemplateParser {
    @Override
    public boolean isValid(String input) {
        return false;
    }

    @Override
    public Template parseTemplate(Reader reader) throws IOException {
        return null;
    }
}
