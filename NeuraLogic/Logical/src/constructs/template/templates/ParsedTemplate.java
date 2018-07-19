package constructs.template.templates;

import constructs.WeightedPredicate;
import constructs.template.metadata.PredicateMetadata;
import constructs.template.metadata.TemplateMetadata;
import constructs.template.metadata.WeightMetadata;
import ida.utils.tuples.Pair;
import networks.structure.Weight;

import java.util.List;
import java.util.logging.Logger;

public abstract class ParsedTemplate {
    private static final Logger LOG = Logger.getLogger(ParsedTemplate.class.getName());

    public List<Pair<WeightedPredicate, PredicateMetadata>> predicatesMetadata;
    public List<Pair<Weight, WeightMetadata>> weightsMetadata;
    public TemplateMetadata templateMetadata;

    public String originalString;
}
