package cz.cvut.fel.ida.logic.grounding;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.logic.HornClause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.constructs.Conjunction;
import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.GroundExample;
import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.grounding.bottomUp.BottomUp;
import cz.cvut.fel.ida.logic.grounding.topDown.TopDown;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.Timing;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

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
    public Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> rulesAndFacts(LiftedExample example, Template template) {
        Map<HornClause, List<WeightedRule>> ruleMap;
        Map<Literal, ValuedFact> atomMap = mapToLogic(example.flatFacts, example.conjunctions);

        if (example.rules.isEmpty()) {
            if (template.hornClauses == null){
                template.hornClauses = rulesToHornClauses(template.rules);
            }
            ruleMap = template.hornClauses;
            //rules.addAll(template.constraints) todo what to do with constraints?
        } else {
            final int totalSize = template.rules.size() + example.rules.size();
            LinkedHashSet<WeightedRule> rules = new LinkedHashSet<>((int)(totalSize / 0.75f + 1), 0.75f);
            rules.addAll(template.rules);
            rules.addAll(example.rules);
            ruleMap = rulesToHornClauses(rules);
        }
        return new Pair<>(ruleMap, atomMap);
    }

    public Map<Literal, ValuedFact> templateFacts(Template template) {
        int capacity = (int)(template.facts.size() / 0.75f + 1);
        Map<Literal, ValuedFact> map = new Object2ObjectOpenHashMap<>(capacity, 0.75f);

        for (ValuedFact vf : template.facts) {
            map.merge(vf.getLiteral(), vf, this::merge2facts);
        }

        return map;
    }

    private LinkedHashMap<HornClause, List<WeightedRule>> rulesToHornClauses(Set<WeightedRule> rules) {
        return rules.stream().collect(Collectors.toMap(WeightedRule::toHornClause, k -> new ArrayList<>(Collections.singletonList(k)), this::merge2rules, LinkedHashMap::new));
    }

    /**
     * @param raf
     * @return
     */
    public Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> mapToLogic(Pair<Set<WeightedRule>, Set<ValuedFact>> raf) {
        Map<HornClause, List<WeightedRule>> ruleMap = rulesToHornClauses(raf.r);
        Map<Literal, ValuedFact> factMap = mapToLogic(raf.s);
        return new Pair<>(ruleMap, factMap);
    }

    public Map<Literal, ValuedFact> mapToLogic(Set<ValuedFact> exampleFacts, Set<Conjunction> exampleConjunctions) {
        int capacity = exampleFacts.size();
        for (Conjunction c : exampleConjunctions) capacity += c.facts.size();

        Map<Literal, ValuedFact> map = new Object2ObjectOpenHashMap<>((int) (capacity / 0.75f + 1));
        for (ValuedFact vf : exampleFacts) {
            map.merge(vf.getLiteral(), vf, this::merge2facts);
        }

        for (Conjunction c : exampleConjunctions) {
            final int size = c.facts.size();
            for (int i = 0; i < size; i++) {
                ValuedFact vf = c.facts.get(i);
                map.merge(vf.getLiteral(), vf, this::merge2facts);
            }
        }
        return map;
    }

    public Map<Literal, ValuedFact> mapToLogic(Set<ValuedFact> facts) {
        int size = facts.size();
        int capacity = (int)(size / 0.75f + 1);
        Map<Literal, ValuedFact> map = new LinkedHashMap<>(capacity, 0.75f);

        for (ValuedFact vf : facts) {
            map.merge(vf.getLiteral(), vf, this::merge2facts);  // Single operation!
        }
        return map;
    }

    public Set<Literal> getAllFacts(GroundExample example) {
        final Set<ValuedFact> collect = example.conjunctions.stream().flatMap(conj -> conj.facts.stream()).collect(Collectors.toSet());
        collect.addAll(example.flatFacts);
        return collect.stream().map(l -> l.literal).collect(Collectors.toSet());
    }

    /**
     * On a clash of two WeightedRules having the same underlying HornClause logic.
     *
     * @param a
     * @param b
     * @returnst
     */
    private List<WeightedRule> merge2rules(List<WeightedRule> a, List<WeightedRule> b) {
        LOG.severe("Two rules with the same logical signature detected! This is most likely by mistake and can cause troubles. Check the template for duplicites around:");
        LOG.severe(a.get(0).getOriginalString());
        LOG.severe(b.get(0).getOriginalString());
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
        Aggregation factAggregation = Aggregation.getFunction(settings.factMergeActivation);
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
            if (sampleList.get(i).query.evidence != sampleList.get(i - 1).query.evidence)   //also merge all the example data (should be the same in most cases, however)
                liftedExample.addAllFrom(sampleList.get(i).query.evidence);
        }

        GroundTemplate groundTemplate = groundRulesAndFacts(liftedExample, template);

        for (GroundingSample sample : sampleList) {
            sample.groundingWrap.setGroundTemplate(groundTemplate);
            sample.groundingWrap.example = liftedExample;
        }
        return sampleList;
    }

}