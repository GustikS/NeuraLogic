package neuralogic.template;

import constructs.example.WeightedFact;
import constructs.template.WeightedPredicate;
import constructs.template.WeightedRule;
import ida.utils.tuples.Pair;
import networks.structure.Weight;
import parsers.neuralogic.NeuralogicParser;
import parsing.Builder;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class XmlTemplateParseTreeExtractor extends TemplateParseTreeExtractor {
    private static final Logger LOG = Logger.getLogger(XmlTemplateParseTreeExtractor.class.getName());

    XmlTemplateParseTreeExtractor(Builder b) {
        super(b);
    }

    @Override
    public List<WeightedRule> getWeightedRules(NeuralogicParser.Template_fileContext ctx) {
        return null;
    }

    @Override
    public List<WeightedFact> getWeightedFacts(NeuralogicParser.Template_fileContext ctx) {
        return null;
    }

    @Override
    public List<Pair<Weight, Map<String, Object>>> getWeightsMetadata(NeuralogicParser.Template_fileContext ctx) {
        return null;
    }

    @Override
    public List<Pair<WeightedPredicate, Map<String, Object>>> getPredicatesMetadata(NeuralogicParser.Template_fileContext ctx) {
        return null;
    }
}
