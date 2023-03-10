package cz.cvut.fel.ida.logic.constructs.building;

import cz.cvut.fel.ida.algebra.utils.metadata.WeightMetadata;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.constructs.Conjunction;
import cz.cvut.fel.ida.logic.constructs.WeightedPredicate;
import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.constructs.template.metadata.PredicateMetadata;
import cz.cvut.fel.ida.logic.constructs.template.metadata.TemplateMetadata;
import cz.cvut.fel.ida.logic.constructs.template.types.ParsedTemplate;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;
import cz.cvut.fel.ida.logic.parsing.grammarParsing.PlainGrammarVisitor;
import cz.cvut.fel.ida.logic.parsing.template.PlainTemplateParseTree;
import cz.cvut.fel.ida.logic.parsing.template.PlainTemplateParseTreeExtractor;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

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
        super(settings, new WeightFactory(settings.inferred.maxWeightCount));   // this weightfactory is the common one to be used later in the process
        this.settings = settings;
    }

    @Override
    public PlainTemplateParseTree parseTreeFrom(Reader reader) {
        if (settings.plaintextInput) {
            try {
                return new PlainTemplateParseTree(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LOG.severe("Formats other than plaintext are not supported yet!");
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

        NeuralogicParser.TemplateFileContext parseTreeRoot = plainParseTree.getRoot();
        List<WeightedRule> weightedRules = templateParseTreeExtractor.getWeightedRules(parseTreeRoot);
        List<ValuedFact> valuedFacts = templateParseTreeExtractor.getWeightedFacts(parseTreeRoot);
        List<Conjunction> weightedConjunctions = templateParseTreeExtractor.getWeightedConjunctions(parseTreeRoot);

        List<Pair<WeightedPredicate, Map<String, Object>>> predicatesMetadata = templateParseTreeExtractor.getPredicatesMetadata(parseTreeRoot);
        List<Pair<Weight, Map<String, Object>>> weightsMetadata = templateParseTreeExtractor.getWeightsMetadata(parseTreeRoot);
        Map<String, Object> templateMetadata = templateParseTreeExtractor.getTemplateMetadata(parseTreeRoot);

        ParsedTemplate template = new ParsedTemplate(weightedRules, valuedFacts);
        template.addConstraints(weightedConjunctions);  //todo check what are weighted conjunctions in template
        template.originalString = plainParseTree.parseTree.getInputStream().getText();    //todo check where is the real string

        if (templateMetadata != null)
            template.templateMetadata = new TemplateMetadata(settings, templateMetadata);
        template.predicatesMetadata = predicatesMetadata.stream().map(pair -> new Pair<>(pair.r, new PredicateMetadata(settings, pair.s))).collect(Collectors.toList());
        template.weightsMetadata = weightsMetadata.stream().map(pair -> new Pair<>(pair.r, new WeightMetadata(settings, pair.s))).collect(Collectors.toList());
        template.containsNegation = negationDetected;

//        settings.inferred.maxWeightCount = weightFactory.getIndex();    //remember the max weight index (to possibly add more valid weights later in the process)

        LOG.fine("Template has been built : " + template);
        return template;
    }

    public Template extendTemplateWith(Reader reader, Settings settings) {
        //TODO later
        return null;
    }
}