package parsing;

import constructs.example.WeightedFact;
import constructs.template.Template;
import constructs.template.WeightedPredicate;
import constructs.template.WeightedRule;
import ida.utils.tuples.Pair;
import networks.structure.Weight;
import neuralogic.ParseTreeProcessor;
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

    /**
     * Build template from a given parse tree and settings
     *
     * @return
     */
    public Template buildFrom(NeuralogicParser parseTree, Settings settings) {

        ParseTreeProcessor parseTreeProcessor = new ParseTreeProcessor(this);

        List<WeightedRule> weightedRules = parseTreeProcessor.new RuleLinesVisitor().visitTemplate_file(parseTree.template_file());
        List<WeightedFact> weightedFacts = parseTreeProcessor.new FactsVisitor().visitTemplate_file(parseTree.template_file());

        List<Pair<WeightedPredicate, Map<String, Object>>> predicatesMetadata = parseTreeProcessor.new PredicatesMetadataVisitor().visitTemplate_file(parseTree.template_file());
        List<Pair<Weight, Map<String, Object>>> weightsMetadata = parseTreeProcessor.new WeightsMetadataVisitor().visitTemplate_file(parseTree.template_file());

        //TODO create template and post-process template
    }

    public Template extendTemplateWith(Reader reader, Settings settings){

    }
}