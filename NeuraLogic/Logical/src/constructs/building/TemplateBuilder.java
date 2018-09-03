package constructs.building;

import constructs.Conjunction;
import constructs.WeightedPredicate;
import constructs.example.ValuedFact;
import constructs.template.Template;
import constructs.template.WeightedRule;
import constructs.template.metadata.PredicateMetadata;
import constructs.template.metadata.WeightMetadata;
import constructs.template.templates.ParsedTemplate;
import ida.utils.tuples.Pair;
import networks.structure.Weight;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.template.PlainTemplateParseTree;
import neuralogic.template.PlainTemplateParseTreeExtractor;
import settings.Settings;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Builds template given some prescribed strategy, output from a parser and some general setting
 */
public class TemplateBuilder extends LogicSourceBuilder<PlainTemplateParseTree, ParsedTemplate> {
    private static final Logger LOG = Logger.getLogger(TemplateBuilder.class.getName());

    public TemplateBuilder(Settings settings) {
        this.settings = settings;
    }

    @Override
    public PlainTemplateParseTree parseTreeFrom(Reader reader) {
        try {
            return new PlainTemplateParseTree(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ParsedTemplate buildTemplateFrom(Reader reader) {
        if (reader != null)
            return buildFrom(parseTreeFrom(reader));
        else
            LOG.severe("No way to create template from sources at request");
        return null;
    }

    @Override
    public ParsedTemplate buildFrom(PlainTemplateParseTree plainParseTree) {
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        return buildFrom(plainParseTree, new PlainTemplateParseTreeExtractor(plainGrammarVisitor));
    }

    /**
     * Build template from a given parse tree and settings
     *
     * @return
     */
    public ParsedTemplate buildFrom(PlainTemplateParseTree plainParseTree, PlainTemplateParseTreeExtractor templateParseTreeExtractor) {

        List<WeightedRule> weightedRules = templateParseTreeExtractor.getWeightedRules(plainParseTree.getRoot());
        List<ValuedFact> valuedFacts = templateParseTreeExtractor.getWeightedFacts(plainParseTree.getRoot());
        List<Conjunction> weightedConjunctions = templateParseTreeExtractor.getWeightedConjunctions(plainParseTree.getRoot());

        List<Pair<WeightedPredicate, Map<String, Object>>> predicatesMetadata = templateParseTreeExtractor.getPredicatesMetadata(plainParseTree.getRoot());
        List<Pair<Weight, Map<String, Object>>> weightsMetadata = templateParseTreeExtractor.getWeightsMetadata(plainParseTree.getRoot());


        ParsedTemplate template = new ParsedTemplate(weightedRules, valuedFacts);
        template.addConstraints(weightedConjunctions);  //todo check what are weighted conjunctions in template
        template.originalString = plainParseTree.toString();    //todo check where is the real string

        template.templateMetadata = null; //TODO add support for this in the template loading
        template.predicatesMetadata = predicatesMetadata.stream().map(pair -> new Pair<>(pair.r, new PredicateMetadata(pair.s))).collect(Collectors.toList());
        template.weightsMetadata = weightsMetadata.stream().map(pair -> new Pair<>(pair.r, new WeightMetadata(pair.s))).collect(Collectors.toList());

        return template;
    }

    public Template extendTemplateWith(Reader reader, Settings settings) {
        //TODO later
        return null;
    }

}