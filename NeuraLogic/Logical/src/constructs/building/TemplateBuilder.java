package constructs.building;

import constructs.Conjunction;
import constructs.WeightedPredicate;
import constructs.example.ValuedFact;
import constructs.template.Template;
import constructs.template.WeightedRule;
import constructs.template.metadata.PredicateMetadata;
import constructs.template.metadata.WeightMetadata;
import ida.utils.tuples.Pair;
import networks.structure.Weight;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import neuralogic.template.PlainTemplateParseTree;
import neuralogic.template.PlainTemplateParseTreeExtractor;
import neuralogic.template.TemplateParseTreeExtractor;
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
public class TemplateBuilder extends LogicSourceBuilder<PlainTemplateParseTree, Template> {
    private static final Logger LOG = Logger.getLogger(TemplateBuilder.class.getName());

    public TemplateBuilder(Settings settings) {
        this.settings = settings;
    }

    @Override
    public Template parseTreeFrom(Reader reader) {
        PlainTemplateParseTree plainParseTree = null;
        try {
            plainParseTree = new PlainTemplateParseTree(reader);
            return buildFrom(plainParseTree);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Template parseTreeFrom(Sources sources) throws IOException {
        if (sources.templateReader != null)
            return parseTreeFrom(sources.templateReader);
        else
            LOG.severe("No way to create template from sources at request");
        return null;
    }

    @Override
    public Template buildFrom(PlainTemplateParseTree plainParseTree) {
        PlainGrammarVisitor plainGrammarVisitor = new PlainGrammarVisitor(this);
        return buildFrom(plainParseTree, new PlainTemplateParseTreeExtractor(plainGrammarVisitor));
    }

    /**
     * Build template from a given parse tree and settings
     *
     * @return
     */
    public Template buildFrom(PlainTemplateParseTree plainParseTree, TemplateParseTreeExtractor templateParseTreeExtractor) {

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

        return template;
    }

    public Template extendTemplateWith(Reader reader, Settings settings) {
        //TODO
        return null;
    }

}