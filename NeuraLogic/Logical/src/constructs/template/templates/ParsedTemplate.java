package constructs.template.templates;

import constructs.template.Template;
import constructs.template.metadata.Metadata;

import java.util.logging.Logger;

public class ParsedTemplate extends Template {
    private static final Logger LOG = Logger.getLogger(ParsedTemplate.class.getName());

    Metadata metadata;
    public String originalString;
}
