package cz.cvut.fel.ida.logic.constructs.template.types;

import cz.cvut.fel.ida.algebra.utils.metadata.WeightMetadata;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.constructs.template.metadata.TemplateMetadata;
import cz.cvut.fel.ida.logic.constructs.WeightedPredicate;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.constructs.template.metadata.PredicateMetadata;
import cz.cvut.fel.ida.utils.generic.Pair;

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
