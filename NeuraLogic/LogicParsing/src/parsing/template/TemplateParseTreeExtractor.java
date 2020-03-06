package parsing.template;

import com.sun.istack.internal.NotNull;
import constructs.Conjunction;
import constructs.WeightedPredicate;
import constructs.example.ValuedFact;
import constructs.template.components.WeightedRule;
import networks.structure.components.weights.Weight;
import parsing.antlr.NeuralogicParser;
import parsing.grammarParsing.GrammarVisitor;
import utils.generic.Pair;

import java.util.List;
import java.util.Map;

public abstract class TemplateParseTreeExtractor<T extends GrammarVisitor> {

    T visitor;

    TemplateParseTreeExtractor(T v) {
        this.visitor = v;
    }

    public abstract List<WeightedRule> getWeightedRules(@NotNull NeuralogicParser.TemplateFileContext ctx);

    public abstract List<ValuedFact> getWeightedFacts(@NotNull NeuralogicParser.TemplateFileContext ctx);

    public abstract List<Conjunction> getWeightedConjunctions(@NotNull NeuralogicParser.TemplateFileContext ctx);

    public abstract List<Pair<Weight, Map<String, Object>>> getWeightsMetadata(@NotNull NeuralogicParser.TemplateFileContext ctx);

    /**
     * Predicate offsets are being set as a part of this
     * @param ctx
     * @return
     */
    public abstract List<Pair<WeightedPredicate, Map<String, Object>>> getPredicatesMetadata(@NotNull NeuralogicParser.TemplateFileContext ctx);

    public abstract Map<String, Object> getTemplateMetadata(@NotNull NeuralogicParser.TemplateFileContext ctx);
}
