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

    static int counter = 0;

    protected String name;

    public LinkedHashSet<WeightedRule> rules;

    public LinkedHashSet<ValuedFact> facts;
    public LinkedHashSet<Conjunction> constraints;  //todo how to handle these?

    @Nullable
    transient Set<Literal> inferredLiterals;

    /**
     * This is merely for computational reuse (it can be computed any time from the rules).
     */
    @Nullable
    transient public Map<HornClause, List<WeightedRule>> hornClauses;

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
     * @return
     */
    @Override
    public List<Weight> getAllWeights() {
        List<Weight> weightList = new ArrayList<>();
        for (WeightedRule rule : rules) {
            if (rule.getWeight() != null)
                weightList.add(rule.getWeight());
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
                if (bodyAtom.getConjunctWeight() != null)
                    weightList.add(bodyAtom.getConjunctWeight());
            }
        }
        List<Weight> uniqueWeights = filterUnique(weightList);
        return uniqueWeights;
    }

    private List<Weight> filterUnique(List<Weight> weightList) {
        return weightList.stream().distinct().collect(Collectors.toList());
    }

    public void updateWeightsFrom(Map<Integer, Weight> neuralWeights) {
//        Map<Integer, Weight> neuralWeights = neuralModel.mapWeightsToIds();
        List<Weight> templateWeights = getAllWeights();
        for (Weight weight : templateWeights) {
            if (weight.isLearnable()) {
                weight.value = neuralWeights.get(weight.index).value;
            }
        }
    }

    public LinkedHashSet<ValuedFact> getValuedFacts() {
        return facts;
    }

    public Set<Literal> getAllFacts() {
        if (inferredLiterals == null) {
            inferredLiterals = inferTemplateFacts();
            if (inferredLiterals != null)
                inferredLiterals.addAll(facts.stream().map(ValuedFact::getLiteral).collect(Collectors.toList()));
        }
        return inferredLiterals;
    }

    public void setFacts(LinkedHashSet<ValuedFact> facts) {
        this.facts = facts;
    }

    public Set<Literal> inferTemplateFacts() {
        if (facts == null || facts.isEmpty())
            return null;
        if (inferredLiterals == null)
            inferredLiterals = new HashSet<>();

        HerbrandModel herbrandModel = new HerbrandModel();
        Set<Literal> facts = this.facts.stream().map(ValuedFact::getLiteral).collect(Collectors.toSet());
        Set<HornClause> rules = this.rules.stream().map(WeightedRule::toHornClause).collect(Collectors.toSet());
        Collection<Literal> values = herbrandModel.inferLiterals(rules, facts);
        inferredLiterals.addAll(values);
        return inferredLiterals;
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
