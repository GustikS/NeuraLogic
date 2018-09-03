package constructs.template.transforming;

import constructs.template.Template;
import constructs.template.templates.GraphTemplate;
import settings.Settings;

/**
 * Created by gusta on 14.3.17.
 */
public interface TemplateReducing {
    Template reduce(GraphTemplate itemplate);

    static TemplateReducing getReducer(Settings settings) {
        //TODO provide more reducers
        return new LinearChainReducer(settings);
    }
}
