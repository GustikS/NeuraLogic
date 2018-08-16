package constructs.template.templates;

import constructs.template.Atom;
import constructs.template.Template;
import ida.ilp.logic.Clause;

import java.util.LinkedHashSet;
import java.util.logging.Logger;

@Deprecated
public class RichLogicTemplate extends Template {
    private static final Logger LOG = Logger.getLogger(RichLogicTemplate.class.getName());


    LinkedHashSet<Atom> atoms;
    LinkedHashSet<Clause> constraints;

}
