package constructs.template.transforming;

import constructs.example.QueryAtom;
import constructs.template.Template;
import settings.Settings;

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