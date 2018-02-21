package constructs.template.transforming;

import constructs.template.Template;

/**
 * Created by gusta on 14.3.17.
 */
public interface TemplateMerging {
    Template merge(Template a, Template b);
}
