package parsing;

import constructs.example.WeightedFact;
import constructs.template.Template;
import constructs.template.WeightedPredicate;
import constructs.template.WeightedRule;
import ida.utils.tuples.Pair;
import networks.structure.Weight;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.template.PlainTemplateParseTree;
import neuralogic.template.PlainTemplateParseTreeExtractor;
import neuralogic.grammarParsing.PlainParseTree;
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

    Settings settings;

    public TemplateBuilder(Settings settings) {
        this.settings = settings;
    }

    @Override
    public Template buildFrom(Reader reader) throws IOException {
        //TODO switch to different Parsers and Extractors based on file types/structures/settings

        // Plain text grammar-based version of template building
        PlainParseTree plainParseTree = new PlainTemplateParseTree(reader);
        TemplateParseTreeExtractor templateParseTreeExtractor = getTemplateParseTreeExtractor();

        return buildFrom(plainParseTree, templateParseTreeExtractor);

    }

    public TemplateParseTreeExtractor getTemplateParseTreeExtractor() {
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        return new PlainTemplateParseTreeExtractor(plainGrammarVisitor);
    }

    /**
     * Build template from a given parse tree and settings
     *
     * @return
     */
    public Template buildFrom(PlainParseTree<NeuralogicParser.Template_fileContext> plainParseTree, TemplateParseTreeExtractor templateParseTreeExtractor) {

        List<WeightedRule> weightedRules = templateParseTreeExtractor.getWeightedRules(plainParseTree.getRoot());
        List<WeightedFact> weightedFacts = templateParseTreeExtractor.getWeightedFacts(plainParseTree.getRoot());

        List<Pair<WeightedPredicate, Map<String, Object>>> predicatesMetadata = templateParseTreeExtractor.getPredicatesMetadata(plainParseTree.getRoot());
        List<Pair<Weight, Map<String, Object>>> weightsMetadata = templateParseTreeExtractor.getWeightsMetadata(plainParseTree.getRoot());

        //TODO create template and post-process template
    }

    public Template extendTemplateWith(Reader reader, Settings settings) {
        //TODO
        return null;
    }
}