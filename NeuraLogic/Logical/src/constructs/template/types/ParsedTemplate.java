package constructs.template.types;

import constructs.WeightedPredicate;
import constructs.example.ValuedFact;
import constructs.template.Template;
import constructs.template.components.WeightedRule;
import constructs.template.metadata.PredicateMetadata;
import constructs.template.metadata.TemplateMetadata;
import constructs.template.metadata.WeightMetadata;
import networks.structure.components.weights.Weight;
import utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;

/**
 * TODO choose a correct hierarchy of Template types
 */
public class ParsedTemplate extends Template {
    private static final Logger LOG = Logger.getLogger(ParsedTemplate.class.getName());

    public List<Pair<WeightedPredicate, PredicateMetadata>> predicatesMetadata;
    public List<Pair<Weight, WeightMetadata>> weightsMetadata;
    public TemplateMetadata templateMetadata;

    public String originalString;

    public ParsedTemplate(List<WeightedRule> weightedRules, List<ValuedFact> valuedFacts) {
        super(weightedRules,valuedFacts);
    }
}
