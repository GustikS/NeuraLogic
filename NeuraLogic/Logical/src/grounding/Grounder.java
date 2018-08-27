package grounding;

import constructs.example.LiftedExample;
import constructs.example.LogicSample;
import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.Template;
import constructs.template.WeightedRule;
import grounding.bottomUp.BottomUp;
import grounding.topDown.TopDown;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import ida.utils.tuples.Pair;
import networks.structure.NeuralNetwork;
import networks.structure.Weight;
import networks.structure.lrnnTypes.QueryNeuron;
import settings.Settings;
import training.NeuralSample;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Gusta on 06.10.2016.
 */
public abstract class Grounder {
    protected Settings settings;

    protected boolean sharedGroundings;

    public Grounder(Settings settings) {
        this.settings = settings;
        sharedGroundings = settings.sharedGroundings;
    }

    public Stream<NeuralSample> ground(Stream<LogicSample> logicSampleStream, Template template) {
        Stream<NeuralSample> neuralSampleStream = null;
        if (settings.sequentialGrounding) {
            //todo
        } else {
            neuralSampleStream = logicSampleStream.map(logicSample -> new NeuralSample(logicSample.target, ground(logicSample.query, template)));
        }
        return neuralSampleStream;
    }

    public NeuralSample ground(LogicSample logicSample, Template template) {
        NeuralSample neuralSample = new NeuralSample(logicSample.target, ground(logicSample.query, template));
        return neuralSample;
    }

    /**
     * Supervised grounding
     *
     * @param queryAtom
     * @param template
     * @return
     */
    public abstract QueryNeuron ground(QueryAtom queryAtom, Template template);

    /**
     * Unsupervised grounding
     *
     * @param example
     * @param template
     * @return
     */
    public abstract NeuralNetwork ground(LiftedExample example, Template template);

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
     * On a clash of two WeightedRules having the same underlying HornClause logic, add their weights (linearity of differentiation) //todo check
     *
     * @param a
     * @param b
     * @return
     */
    private WeightedRule merge2rules(WeightedRule a, WeightedRule b) {
        WeightedRule weightedRule = new WeightedRule(a);
        weightedRule.weight = new Weight(a.weight.name, a.weight.value.add(b.weight.value), a.weight.isFixed);
        return weightedRule;
    }

    /**
     * On a clash of two ValuedFacts having the same underlying Literal logic, add their weights (linearity of differentiation) //todo check
     *
     * @param a
     * @param b
     * @return
     */
    private ValuedFact merge2facts(ValuedFact a, ValuedFact b) {
        return new ValuedFact(a.getOffsettedPredicate(), a.getLiteral().termList(), a.getLiteral().isNegated(), a.getFactValue().add(b.getFactValue()));
    }

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
}