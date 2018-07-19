package building;

import constructs.Conjunction;
import constructs.WeightedPredicate;
import constructs.example.ValuedFact;
import constructs.template.Template;
import constructs.template.WeightedRule;
import constructs.template.metadata.PredicateMetadata;
import constructs.template.metadata.WeightMetadata;
import constructs.template.transforming.MetadataProcessor;
import ida.utils.tuples.Pair;
import networks.structure.Weight;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.grammarParsing.PlainParseTree;
import neuralogic.template.PlainTemplateParseTree;
import neuralogic.template.PlainTemplateParseTreeExtractor;
import neuralogic.template.TemplateParseTreeExtractor;
import parsers.neuralogic.NeuralogicParser;
import settings.Settings;
import settings.Sources;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Builds template given some prescribed strategy, output from a parser and some general setting
 */
public class TemplateBuilder extends LogicBuilder<Template> {
    private static final Logger LOG = Logger.getLogger(TemplateBuilder.class.getName());

    public TemplateBuilder(Settings settings) {
        this.settings = settings;
    }

    @Override
    public Template buildFrom(Reader reader) throws IOException {
        PlainParseTree plainParseTree = new PlainTemplateParseTree(reader);
        return buildFrom(plainParseTree);
    }

    @Override
    public Template buildFrom(Sources sources) throws IOException {
        if (sources.templateParseTree != null)
            return buildFrom(sources.templateParseTree);
        else if (sources.templateReader != null)
            return buildFrom(sources.templateReader);
        else
            LOG.severe("No way to create template from sources at request");
        return null;
    }

    public Template buildFrom(PlainParseTree<NeuralogicParser.TemplateFileContext> plainParseTree) {
        return buildFrom(plainParseTree, getTemplateParseTreeExtractor());
    }

    private TemplateParseTreeExtractor getTemplateParseTreeExtractor() {
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        return new PlainTemplateParseTreeExtractor(plainGrammarVisitor);
    }

    /**
     * Build template from a given parse tree and settings
     *
     * @return
     */
    public Template buildFrom(PlainParseTree<NeuralogicParser.TemplateFileContext> plainParseTree, TemplateParseTreeExtractor templateParseTreeExtractor) {

        List<WeightedRule> weightedRules = templateParseTreeExtractor.getWeightedRules(plainParseTree.getRoot());
        List<ValuedFact> valuedFacts = templateParseTreeExtractor.getWeightedFacts(plainParseTree.getRoot());
        List<Conjunction> weightedConjunctions = templateParseTreeExtractor.getWeightedConjunctions(plainParseTree.getRoot());

        List<Pair<WeightedPredicate, Map<String, Object>>> predicatesMetadata = templateParseTreeExtractor.getPredicatesMetadata(plainParseTree.getRoot());
        List<Pair<Weight, Map<String, Object>>> weightsMetadata = templateParseTreeExtractor.getWeightsMetadata(plainParseTree.getRoot());


        Template template = new Template(weightedRules, valuedFacts);
        template.addConstraints(weightedConjunctions);  //todo check what are weighted conjunctions in template
        template.originalString = plainParseTree.toString();    //todo check where is the real string

        template.templateMetadata = null; //TODO add support for this in the template file
        template.predicatesMetadata = predicatesMetadata.stream().map(pair -> new Pair<>(pair.r, new PredicateMetadata(pair.s))).collect(Collectors.toList());
        template.weightsMetadata = weightsMetadata.stream().map(pair -> new Pair<>(pair.r, new WeightMetadata(pair.s))).collect(Collectors.toList());

        template = postprocessTemplate(template);   //TODO turn this into a new inner template processing pipeline!!

        return template;
    }

    public Template extendTemplateWith(Reader reader, Settings settings) {
        //TODO
        return null;
    }

}