package cz.cvut.fel.ida.logic.constructs.template.types;

import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.HeadAtom;
import cz.cvut.fel.ida.logic.Clause;

import java.util.LinkedHashSet;
import java.util.logging.Logger;

@Deprecated
public class RichLogicTemplate extends Template {
    private static final Logger LOG = Logger.getLogger(RichLogicTemplate.class.getName());


    LinkedHashSet<HeadAtom> atoms;
    LinkedHashSet<Clause> constraints;

}
