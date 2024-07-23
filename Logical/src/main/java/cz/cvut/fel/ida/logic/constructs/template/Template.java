package cz.cvut.fel.ida.logic.constructs.template;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.learning.Model;
import cz.cvut.fel.ida.logic.HornClause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.constructs.Conjunction;
import cz.cvut.fel.ida.logic.constructs.example.QueryAtom;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.BodyAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.HeadAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.constructs.template.types.GraphTemplate;
import cz.cvut.fel.ida.logic.subsumption.HerbrandModel;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Gusta on 04.10.2016.
 */
public class Template implements Model<QueryAtom>, Exportable {
    private static final Logger LOG = Logger.getLogger(Template.class.getName());

    /**
     * counter used just for naming
     */
    static int counter = 0;
    protected String name;

    public LinkedHashSet<WeightedRule> rules;
    public LinkedHashSet<ValuedFact> facts;
    @Nullable
    public LinkedHashSet<Conjunction> constraints;  //todo how to handle these?

    /**
     * Good to know for stratification checking
     */
    public boolean containsNegation = false;

    /**
     * Template's own inference engine, storing preprocessed structures
     */
    public transient HerbrandModel herbrandModel;

    /**
     * Atoms inferred on top of the given {@link #facts} using the {@link #herbrandModel}
     */
    @Nullable
    public transient Set<Literal> inferredAtoms;
    /**
     * All possible atoms altogether ({@link #facts} +  {@link #inferredAtoms})
     */
    @Nullable
    private transient Set<Literal> allAtoms;

    /**
     * This is merely for computational reuse (it can be computed any time from the rules).
     */
    @Nullable
    public transient Map<HornClause, List<WeightedRule>> hornClauses;

    public Template() {
        this.name = "template" + counter++;
        this.rules = new LinkedHashSet<>();
        this.facts = new LinkedHashSet<>();
        this.constraints = new LinkedHashSet<>();
    }

    public Template(Template other) {
        this.name = "template" + counter++;
        this.rules = other.rules;
        this.facts = other.facts;
        this.constraints = other.constraints;
        this.containsNegation = other.containsNegation;
    }

    public Template(List<WeightedRule> rules, List<ValuedFact> facts) {
        this();
        this.rules.addAll(rules);
        this.facts.addAll(facts);
    }

    public void addConstraints(List<Conjunction> constr) {
        this.constraints = new LinkedHashSet<>(constr);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Value evaluate(QueryAtom query) {
        return null;
    }

    /**
     * Return UNIQUE weights list
     *
     * @return
     */
    @Override
    public List<Weight> getAllWeights() {
        List<Weight> weightList = new ArrayList<>();
        for (WeightedRule rule : rules) {
            if (rule.getWeight() != null) weightList.add(rule.getWeight());
            Weight offset = rule.getOffset();
            if (offset != null) {
                offset.isOffset = true;
                weightList.add(offset);
            }
            Weight headOffset = rule.getHead().getOffset();
            if (headOffset != null) {
                headOffset.isOffset = true;
                weightList.add(headOffset);
            }
            for (BodyAtom bodyAtom : rule.getBody()) {
                if (bodyAtom.getConjunctWeight() != null) weightList.add(bodyAtom.getConjunctWeight());
            }
        }
        for (ValuedFact fact : facts) {
            if (fact.weight != null) {
                weightList.add(fact.weight);
            }
        }
        List<Weight> uniqueWeights = filterUnique(weightList);
        return uniqueWeights;
    }

    private List<Weight> filterUnique(List<Weight> weightList) {
        return weightList.stream().distinct().collect(Collectors.toList());
    }

    public void updateWeightsFrom(Map<Integer, Weight> neuralWeights) {
        List<Weight> templateWeights = getAllWeights();
        for (Weight weight : templateWeights) {
            if (weight.isLearnable()) {
                weight.value = neuralWeights.get(weight.index).value;
            }
        }
    }

    public Set<Literal> getAllAtoms(boolean inferAtoms) {
        if (allAtoms != null) {
            return allAtoms;
        }
        allAtoms = facts.stream().map(ValuedFact::getLiteral).collect(Collectors.toSet());
        if (inferredAtoms == null) {
            preprocessInference(inferAtoms);
        }
        allAtoms.addAll(inferredAtoms);
        return allAtoms;
    }

    public void setFacts(LinkedHashSet<ValuedFact> facts) {
        this.facts = facts;
    }

    public void preprocessInference(boolean inferAtoms) {
        if (inferredAtoms == null) {
            inferredAtoms = new HashSet<>();
        }
        for (Iterator<ValuedFact> iterator = facts.iterator(); iterator.hasNext(); ) {
            ValuedFact fact = iterator.next();
            if (fact.literal.containsVariable()) {
                iterator.remove();
                WeightedRule weightedRule = new WeightedRule();
                weightedRule.setOriginalString(fact.originalString);
                HeadAtom headAtom = new HeadAtom(fact.offsettedPredicate, fact.literal.termList());
                weightedRule.setHead(headAtom);
                weightedRule.setWeight(fact.weight);
                weightedRule.setHashCode(headAtom.hashCode());
                weightedRule.setBody(new LinkedList<>());
                rules.add(weightedRule);
            }
        }

        LinkedHashSet<Literal> facts = this.facts.stream().map(ValuedFact::getLiteral).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<HornClause> rules = this.rules.stream().map(WeightedRule::toHornClause).collect(Collectors.toCollection(LinkedHashSet::new));

        herbrandModel = new HerbrandModel(facts, rules);
        if (inferAtoms) {
            Collection<Literal> atoms = herbrandModel.inferAtoms();
            inferredAtoms.addAll(atoms);
        }
    }

    public GraphTemplate prune(QueryAtom query) {
        LOG.warning("Inefficient template pruning");
        return new GraphTemplate(this).prune(query);
    }

    public void addAllFrom(Template template) {
        if (template == this) {
            return;
        }
        rules.addAll(template.rules);
        facts.addAll(template.facts);
        constraints.addAll(template.constraints);
    }

    @Override
    public String toString() {
        return name + ":= rules: " + rules.size() + ", facts: " + facts.size() + ", constraints: " + constraints.size();
    }
}
