package cz.cvut.fel.ida.logic.grounding;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.logic.HornClause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.grounding.bottomUp.BottomUp;
import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.Timing;
import cz.cvut.fel.ida.logic.grounding.topDown.TopDown;
import cz.cvut.fel.ida.setup.Settings;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class responsible for logical inference/grounding, creating GroundTemplate (set of ground rules and facts) from lifted Template and Example
 * <p>
 * Created by Gusta on 06.10.2016.
 */
public abstract class Grounder implements Exportable {
    private static final Logger LOG = Logger.getLogger(Grounder.class.getName());
    transient public Settings settings;
    public WeightFactory weightFactory;

    public Timing timing;

    public Grounder(Settings settings) {
        this(settings, new WeightFactory(settings.inferred.maxWeightCount));
    }

    public Grounder(Settings settings, WeightFactory weightFactory) {
        this.settings = settings;
        this.weightFactory = weightFactory;
        this.timing = new Timing();
    }

    //todo should also decide based on template sturcture?
    public static Grounder getGrounder(Settings settings) {
        switch (settings.grounding) {
            case BUP:
                return new BottomUp(settings);
            case TDOWN:
                return new TopDown(settings);
            default:
                return new BottomUp(settings);
        }
    }

    /**
     * The theorem proving part - returns ground rules and facts wrapped in a GroundTemplate - to be implemented by subclasses
     *
     * @param example
     * @param template
     * @return
     */
    public abstract GroundTemplate groundRulesAndFacts(LiftedExample example, Template template);

    /**
     * The theorem proving part - with reuse of some previous grounding "memory"
     *
     * @param example
     * @param template
     * @param memory
     * @return
     */
    public abstract GroundTemplate groundRulesAndFacts(LiftedExample example, Template template, GroundTemplate memory);

    /**
     * Extracting set of rules and facts from the merge of an example and template
     *
     * @param example
     * @param template
     * @return
     */
    public Pair<Set<WeightedRule>, Set<ValuedFact>> rulesAndFacts(LiftedExample example, Template template) {
        LinkedHashSet<ValuedFact> flatFacts;
        if (!template.facts.isEmpty() || !example.conjunctions.isEmpty()) {
            flatFacts = new LinkedHashSet<>(example.flatFacts);
            flatFacts.addAll(example.conjunctions.stream().flatMap(conj -> conj.facts.stream()).collect(Collectors.toList()));
            flatFacts.addAll(template.facts);
        } else {
            flatFacts = example.flatFacts;
        }

        LinkedHashSet<WeightedRule> rules;
        if (example.rules.isEmpty()) {
            rules = template.rules;
            //rules.addAll(template.constraints) todo what to do with constraints?
        } else {
            rules = new LinkedHashSet<>(template.rules);
            rules.addAll(example.rules);
        }
        return new Pair<>(rules, flatFacts);
    }

    /**
     * @param raf
     * @return
     */
    public Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> mapToLogic(Pair<Set<WeightedRule>, Set<ValuedFact>> raf) {
        Map<HornClause, List<WeightedRule>> ruleMap = raf.r.stream().collect(Collectors.toMap(WeightedRule::toHornClause, Arrays::asList, this::merge2rules, LinkedHashMap::new));
        Map<Literal, ValuedFact> factMap = mapToLogic(raf.s);
        return new Pair<>(ruleMap, factMap);
    }

    /**
     * @param facts
     * @return
     */
    public Map<Literal, ValuedFact> mapToLogic(Set<ValuedFact> facts) {
        return facts.stream().collect(Collectors.toMap(ValuedFact::getLiteral, vf -> vf, this::merge2facts));
    }

    /**
     * On a clash of two WeightedRules having the same underlying HornClause logic.
     *
     * @param a
     * @param b
     * @returnst
     */
    private List<WeightedRule> merge2rules(List<WeightedRule> a, List<WeightedRule> b) {
        LOG.severe("Two rules with the same logical signature! Check the template for duplicites...");
        a.addAll(b);
        return a;
    }

    /**
     * On a clash of two ValuedFacts having the same underlying Literal logic, take their max or other settings.factMergeActivation directly now.
     *
     * @param a
     * @param b
     * @return
     */
    private ValuedFact merge2facts(ValuedFact a, ValuedFact b) {
        Aggregation factAggregation = Aggregation.getAggregation(settings.factMergeActivation);
        Value evaluation = factAggregation.evaluate(Arrays.asList(a.getValue(), b.getValue()));
        return new ValuedFact(a.getOffsettedPredicate(), a.getLiteral().termList(), a.getLiteral().isNegated(), weightFactory.construct("foo", evaluation, true, true));
    }

    /**
     * Consume all samples, share all facts and rules between them, then ground as a single big sample.
     *
     * @param sampleList
     * @return
     */
    public List<GroundingSample> globalGroundingSample(List<GroundingSample> sampleList) {
        LOG.info("Global grounding for " + sampleList.size() + " samples.");
        Template template = new Template(sampleList.get(0).template);   //just get the template from the 1st sample - it should be the same for all samples
        LiftedExample liftedExample = new LiftedExample();

        for (int i = 1; i < sampleList.size(); i++) {
            if (sampleList.get(i).template != sampleList.get(i - 1).template)   //if the template differs across the samples, merge them all into one
                template.addAllFrom(sampleList.get(i).template);
        }
        liftedExample.addAllFrom(sampleList.get(0).query.evidence);
        for (int i = 1; i < sampleList.size(); i++) {
            if (sampleList.get(i).query.evidence != sampleList.get(i - 1).query.evidence)   //also marge all the example data (should be the same in most cases, however)
                liftedExample.addAllFrom(sampleList.get(i).query.evidence);
        }

        GroundTemplate groundTemplate = groundRulesAndFacts(liftedExample, template);

        sampleList.forEach(sample -> sample.groundingWrap.setGroundTemplate(groundTemplate));
        return sampleList;
    }

}