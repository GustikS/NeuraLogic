package cz.cvut.fel.ida.logic.constructs.template.transforming;

import cz.cvut.fel.ida.logic.constructs.example.QueryAtom;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.setup.Settings;

/**
 * Created by gusta on 14.3.17.
 */
public interface TemplateReducing {
    <T extends Template> T reduce(T itemplate);

    <T extends Template> T reduce(T itemplate, QueryAtom queryAtom);

    static TemplateReducing getReducer(Settings settings) {
        return new TemplateChainReducer(settings);
    }
}