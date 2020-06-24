package cz.cvut.fel.ida.logic.parsing.template;

import cz.cvut.fel.ida.logic.constructs.Conjunction;
import cz.cvut.fel.ida.logic.constructs.WeightedPredicate;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;
import cz.cvut.fel.ida.logic.parsing.grammarParsing.GrammarVisitor;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Deprecated
public class XmlTemplateParseTreeExtractor extends TemplateParseTreeExtractor {
    private static final Logger LOG = Logger.getLogger(XmlTemplateParseTreeExtractor.class.getName());

    XmlTemplateParseTreeExtractor(GrammarVisitor v) {
        super(v);
    }


    @Override
    public List<WeightedRule> getWeightedRules(NeuralogicParser.TemplateFileContext ctx) {
        return null;
    }

    @Override
    public List<ValuedFact> getWeightedFacts(NeuralogicParser.TemplateFileContext ctx) {
        return null;
    }

    @Override
    public List<Conjunction> getWeightedConjunctions(NeuralogicParser.TemplateFileContext ctx) {
        return null;
    }

    @Override
    public List<Pair<Weight, Map<String, Object>>> getWeightsMetadata(NeuralogicParser.TemplateFileContext ctx) {
        return null;
    }

    @Override
    public List<Pair<WeightedPredicate, Map<String, Object>>> getPredicatesMetadata(NeuralogicParser.TemplateFileContext ctx) {
        return null;
    }

    @Override
    public Map<String, Object> getTemplateMetadata(NeuralogicParser.TemplateFileContext ctx) {
        return null;
    }
}
