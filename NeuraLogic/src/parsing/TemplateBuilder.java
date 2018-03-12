package parsing;

import constructs.example.WeightedFact;
import constructs.template.Template;
import constructs.template.WeightedPredicate;
import constructs.template.WeightedRule;
import ida.utils.tuples.Pair;
import networks.structure.Weight;
import neuralogic.grammarVisitors.PlainGrammarVisitor;
import neuralogic.template.PlainTemplateParseTree;
import neuralogic.template.PlainTemplateParseTreeExtractor;
import neuralogic.ParseTree;
import neuralogic.template.TemplateParseTreeExtractor;
import parsers.neuralogic.NeuralogicParser;
import settings.Settings;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Builds template given some prescribed strategy, output from a parser and some general setting
 */
public class TemplateBuilder extends Builder<Template> {
    private static final Logger LOG = Logger.getLogger(TemplateBuilder.class.getName());


    public TemplateBuilder(Settings settings) {

    }

    @Override
    public Template buildFrom(Reader reader, Settings settings) throws IOException {
        //TODO switch to different Parsers and Extractors based on file types/structures/settings

        // Plain text grammar-based version of template building
        ParseTree parseTree = new PlainTemplateParseTree(reader);
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        TemplateParseTreeExtractor templateParseTreeExtractor = new PlainTemplateParseTreeExtractor(plainGrammarVisitor);

        return buildFrom(parseTree, templateParseTreeExtractor, settings);

    }

    /**
     * Build template from a given parse tree and settings
     *
     * @return
     */
    public Template buildFrom(ParseTree<NeuralogicParser.Template_fileContext> parseTree, TemplateParseTreeExtractor templateParseTreeExtractor, Settings settings) {

        List<WeightedRule> weightedRules = templateParseTreeExtractor.getWeightedRules(parseTree.getRoot());
        List<WeightedFact> weightedFacts = templateParseTreeExtractor.getWeightedFacts(parseTree.getRoot());

        List<Pair<WeightedPredicate, Map<String, Object>>> predicatesMetadata = templateParseTreeExtractor.getPredicatesMetadata(parseTree.getRoot());
        List<Pair<Weight, Map<String, Object>>> weightsMetadata = templateParseTreeExtractor.getWeightsMetadata(parseTree.getRoot());

        //TODO create template and post-process template
    }

    public Template extendTemplateWith(Reader reader, Settings settings) {

    }
}