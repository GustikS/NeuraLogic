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
import ida.utils.tuples.Pair;
import settings.Settings;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class responsible for logical inference/grounding, creating GroundTemplate (set of ground rules and facts) from lifted Template and Example
 *
 * Created by Gusta on 06.10.2016.
 *
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

    //todo next should also decide based on template sturcture?
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
     * @param example
     * @param template
     * @return
     */
    public Pair<Set<WeightedRule>, Set<ValuedFact>> rulesAndFacts(LiftedExample example, Template template) {
        LinkedHashSet<ValuedFact> flatFacts = new LinkedHashSet<>(example.flatFacts);
        flatFacts.addAll(example.conjunctions.stream().flatMap(conj -> conj.facts.stream()).collect(Collectors.toList()));
        flatFacts.addAll(template.facts);

        LinkedHashSet<WeightedRule> rules = new LinkedHashSet<>(template.rules);
        //rules.addAll(template.constraints) todo what to do with constraints?
        rules.addAll(example.rules);
        return new Pair<>(rules, flatFacts);
    }

    /**
     * Todo optimize this for the case when the template doesnt change with every example (remember the template rules)
     *
     * @param raf
     * @return
     */
    public Pair<Map<HornClause, WeightedRule>, Map<Literal, ValuedFact>> mapToLogic(Pair<Set<WeightedRule>, Set<ValuedFact>> raf) {
        Map<HornClause, WeightedRule> ruleMap = raf.r.stream().collect(Collectors.toMap(WeightedRule::toHornClause, wr -> wr, this::merge2rules));
        Map<Literal, ValuedFact> factMap = raf.s.stream().collect(Collectors.toMap(ValuedFact::getLiteral, vf -> vf, this::merge2facts));
        return new Pair<>(ruleMap, factMap);
    }

    /**
     * On a clash of two WeightedRules having the same underlying HornClause logic, add their weights (linearity of differentiation).
     * But must be careful to check for edge cases -> outsourcing to weightFactory
     *
     * @param a
     * @param b
     * @return
     */
    private WeightedRule merge2rules(WeightedRule a, WeightedRule b) {
        WeightedRule weightedRule = new WeightedRule(a);
        weightedRule.weight = weightFactory.mergeWeights(a.weight,b.weight);
        return weightedRule;
    }

    /**
     * On a clash of two ValuedFacts having the same underlying Literal logic, add their Values (linearity of differentiation) //todo check
     *
     * @param a
     * @param b
     * @return
     */
    private ValuedFact merge2facts(ValuedFact a, ValuedFact b) {
        return new ValuedFact(a.getOffsettedPredicate(), a.getLiteral().termList(), a.getLiteral().isNegated(), a.getFactValue().plus(b.getFactValue()));
    }

    /**
     * Consume all samples, share all facts and rules between them, then ground as a single big sample.
     * Stream TERMINATING operation!    //todo take out the stream...
     *
     * @param samples
     * @return
     */
    public Stream<GroundingSample> globalGroundingSample(Stream<GroundingSample> samples) {
        List<GroundingSample> sampleList = samples.collect(Collectors.toList());
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

        sampleList.forEach(sample -> sample.grounding.setGrounding(groundTemplate));
        return sampleList.stream();
    }
}