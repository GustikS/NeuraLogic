package constructs.template.templates;

import constructs.template.BodyAtom;
import constructs.template.Template;
import constructs.template.WeightedRule;
import ida.ilp.logic.Predicate;
import ida.ilp.logic.subsumption.Matching;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GraphTemplate extends Template {
    private static final Logger LOG = Logger.getLogger(GraphTemplate.class.getName());

    Map<BodyAtom, List<WeightedRule>> atoms2rules;


    Matching matching;

    public GraphTemplate(Template template){
        super(template);

        atoms2rules = new HashMap<>();

        matching = new Matching();

        Map<Predicate, WeightedRule> predicate2rules = new HashMap<>();


    }
}
