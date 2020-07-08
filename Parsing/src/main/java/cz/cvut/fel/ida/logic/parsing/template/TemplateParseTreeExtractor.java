package cz.cvut.fel.ida.logic.parsing.template;

import org.jetbrains.annotations.NotNull;
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
