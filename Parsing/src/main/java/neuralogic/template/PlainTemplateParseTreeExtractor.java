package neuralogic.template;

import com.sun.istack.internal.NotNull;
import constructs.example.WeightedFact;
import constructs.template.WeightedPredicate;
import constructs.template.WeightedRule;
import ida.utils.tuples.Pair;
import networks.structure.Weight;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import parsers.neuralogic.NeuralogicBaseVisitor;
import parsers.neuralogic.NeuralogicParser;
import parsers.neuralogic.NeuralogicParser.Template_fileContext;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Parsing text files for contained template given a antlr4 grammar, using generated Lexer and Parser
 * Processing of the parse tree implemented using the Visitor pattern as inspired by http://jakubdziworski.github.io/java/2016/04/01/antlr_visitor_vs_listener.html
 * <p>
 * STATE: contains factories
 * Created by gusta on 1.3.18.
 */
public class PlainTemplateParseTreeExtractor extends TemplateParseTreeExtractor<PlainGrammarVisitor> {

    private static final Logger LOG = Logger.getLogger(PlainTemplateParseTreeExtractor.class.getName());

    public PlainTemplateParseTreeExtractor(PlainGrammarVisitor visitor) {
        super(visitor);
    }

    @Override
    public List<WeightedRule> getWeightedRules(@NotNull Template_fileContext ctx) {
        return new RuleLinesVisitor().visitTemplate_file(ctx);
    }

    @Override
    public List<WeightedFact> getWeightedFacts(@NotNull Template_fileContext ctx) {
        return new FactsVisitor().visitTemplate_file(ctx);
    }

    @Override
    public List<Pair<Weight, Map<String, Object>>> getWeightsMetadata(@NotNull Template_fileContext ctx) {
        return new WeightsMetadataVisitor().visitTemplate_file(ctx);
    }

    @Override
    public List<Pair<WeightedPredicate, Map<String, Object>>> getPredicatesMetadata(@NotNull Template_fileContext ctx) {
        return new PredicatesMetadataVisitor().visitTemplate_file(ctx);
    }

    public class RuleLinesVisitor extends NeuralogicBaseVisitor<List<WeightedRule>> {

        @Override
        public List<WeightedRule> visitTemplate_file(@NotNull NeuralogicParser.Template_fileContext ctx) {

            List<NeuralogicParser.Template_lineContext> template_lines = ctx.template_line();

            PlainGrammarVisitor.RuleLineVisitor ruleLineVisitor = visitor.new RuleLineVisitor();
            List<WeightedRule> rules = template_lines.stream()
                    .filter(line -> line.lrnn_rule() != null)
                    .map(line -> line.lrnn_rule().accept(ruleLineVisitor))
                    .collect(Collectors.toList());

            return rules;
        }
    }

    public class WeightsMetadataVisitor extends NeuralogicBaseVisitor<List<Pair<Weight, Map<String, Object>>>> {

        @Override
        public List<Pair<Weight, Map<String, Object>>> visitTemplate_file(@NotNull NeuralogicParser.Template_fileContext ctx) {

            List<NeuralogicParser.Template_lineContext> template_lines = ctx.template_line();

            PlainGrammarVisitor.WeightMetadataVisitor weightMetadataVisitor = visitor.new WeightMetadataVisitor();
            List<Pair<Weight, Map<String, Object>>> weightMetadataList = template_lines.stream()
                    .filter(line -> line.weight_metadata() != null)
                    .map(line -> line.lrnn_rule().accept(weightMetadataVisitor))
                    .collect(Collectors.toList());

            return weightMetadataList;
        }
    }

    public class PredicatesMetadataVisitor extends NeuralogicBaseVisitor<List<Pair<WeightedPredicate, Map<String, Object>>>> {

        @Override
        public List<Pair<WeightedPredicate, Map<String, Object>>> visitTemplate_file(@NotNull NeuralogicParser.Template_fileContext ctx) {

            List<NeuralogicParser.Template_lineContext> template_lines = ctx.template_line();

            PlainGrammarVisitor.PredicateOffsetVisitor predicateOffsetVisitor = visitor.new PredicateOffsetVisitor();
            template_lines.stream()
                    .filter(line -> line.predicate_offset() != null)
                    .map(line -> line.predicate_offset().accept(predicateOffsetVisitor))
                    .collect(Collectors.toList());
            //no need to do anything, offsets are being set during visiting

            PlainGrammarVisitor.PredicateMetadataVisitor predicateMetadataVisitor = visitor.new PredicateMetadataVisitor();
            List<Pair<WeightedPredicate, Map<String, Object>>> predicateMetadataList = template_lines.stream()
                    .filter(line -> line.predicate_metadata() != null)
                    .map(line -> line.lrnn_rule().accept(predicateMetadataVisitor))
                    .collect(Collectors.toList());

            return predicateMetadataList;
        }
    }

    public class FactsVisitor extends NeuralogicBaseVisitor<List<WeightedFact>> {

        @Override
        public List<WeightedFact> visitTemplate_file(@NotNull NeuralogicParser.Template_fileContext ctx) {

            List<NeuralogicParser.Template_lineContext> template_lines = ctx.template_line();

            PlainGrammarVisitor.FactVisitor factVisitor = visitor.new FactVisitor();
            List<WeightedFact> facts = template_lines.stream()
                    .filter(line -> line.fact() != null)
                    .map(line -> line.fact().accept(factVisitor))
                    .collect(Collectors.toList());

            return facts;
        }
    }

}