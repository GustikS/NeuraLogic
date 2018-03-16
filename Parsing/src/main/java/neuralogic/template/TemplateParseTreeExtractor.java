package neuralogic.template;

import com.sun.istack.internal.NotNull;
import constructs.example.WeightedFact;
import constructs.template.WeightedPredicate;
import constructs.template.WeightedRule;
import ida.utils.tuples.Pair;
import networks.structure.Weight;
import neuralogic.grammarParsing.GrammarVisitor;
import parsers.neuralogic.NeuralogicParser;

import java.util.List;
import java.util.Map;

public abstract class TemplateParseTreeExtractor<T extends GrammarVisitor> {

    T visitor;

    TemplateParseTreeExtractor(T v) {
        this.visitor = v;
    }

    public abstract List<WeightedRule> getWeightedRules(@NotNull NeuralogicParser.Template_fileContext ctx);

    public abstract List<WeightedFact> getWeightedFacts(@NotNull NeuralogicParser.Template_fileContext ctx);

    public abstract List<Pair<Weight, Map<String, Object>>> getWeightsMetadata(@NotNull NeuralogicParser.Template_fileContext ctx);

    public abstract List<Pair<WeightedPredicate, Map<String, Object>>> getPredicatesMetadata(@NotNull NeuralogicParser.Template_fileContext ctx);
}
