package grounding;

import constructs.building.factories.WeightFactory;
import constructs.example.LiftedExample;
import constructs.example.ValuedFact;
import constructs.template.Template;
import constructs.template.components.WeightedRule;
import grounding.bottomUp.BottomUp;
import grounding.topDown.TopDown;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.Value;
import settings.Settings;
import utils.generic.Pair;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class responsible for logical inference/grounding, creating GroundTemplate (set of ground rules and facts) from lifted Template and Example
 * <p>
 * Created by Gusta on 06.10.2016.
 */
public abstract class Grounder {
    private static final Logger LOG = Logger.getLogger(Grounder.class.getName());
    public Settings settings;
    public WeightFactory weightFactory;

    public Grounder(Settings settings) {
        this.settings = settings;
        this.weightFactory = new WeightFactory();
    }

    public Grounder(Settings settings, WeightFactory weightFactory) {
        this.settings = settings;
        this.weightFactory = weightFactory;
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
        Template template = new Template();
        LiftedExample liftedExample = new LiftedExample();

        template.addAllFrom(sampleList.get(0).template);
        for (int i = 1; i < sampleList.size(); i++) {
            if (sampleList.get(i).template != sampleList.get(i - 1).template)
                template.addAllFrom(sampleList.get(i).template);
        }
        liftedExample.addAllFrom(sampleList.get(0).query.evidence);
        for (int i = 1; i < sampleList.size(); i++) {
            if (sampleList.get(i).query.evidence != sampleList.get(i - 1).query.evidence)
                liftedExample.addAllFrom(sampleList.get(i).query.evidence);
        }

        GroundTemplate groundTemplate = groundRulesAndFacts(liftedExample, template);

        sampleList.forEach(sample -> sample.groundingWrap.setGroundTemplate(groundTemplate));
        return sampleList;
    }
}