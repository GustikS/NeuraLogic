package constructs.template;

import com.sun.istack.internal.Nullable;
import constructs.Conjunction;
import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.components.BodyAtom;
import constructs.template.components.WeightedRule;
import constructs.template.types.GraphTemplate;
import grounding.bottomUp.HerbrandModel;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.Predicate;
import ida.utils.collections.MultiMap;
import learning.Model;
import networks.computation.evaluation.values.Value;
import networks.computation.training.NeuralModel;
import networks.structure.components.weights.Weight;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Gusta on 04.10.2016.
 */
public class Template implements Model<QueryAtom> {
    private static final Logger LOG = Logger.getLogger(Template.class.getName());

    static int counter = 0;

    String id;

    public LinkedHashSet<WeightedRule> rules;

    public LinkedHashSet<ValuedFact> facts;
    public LinkedHashSet<Conjunction> constraints;  //todo how to handle these?

    @Nullable
    Set<Literal> inferredLiterals;

    /**
     * This is merely for computational reuse (it can be computed any time from the rules).
     */
    @Nullable
    public Map<HornClause, List<WeightedRule>> hornClauses;

    public Template() {
        this.id = "template" + counter++;
        this.rules = new LinkedHashSet<>();
        this.facts = new LinkedHashSet<>();
        this.constraints = new LinkedHashSet<>();
    }

    public Template(Template other) {
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
    public String getId() {
        return id;
    }

    @Override
    public Value evaluate(QueryAtom query) {
        return null;
    }

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
        return weightList;
    }

    public void updateWeightsFrom(NeuralModel neural) {
        //TODO
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
        MultiMap<Predicate, Literal> multiMap = herbrandModel.inferModel(rules, facts);
        multiMap.values().forEach(inferredLiterals::addAll);
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
        return id + ", rules: " + rules.size() + ", facts: " + facts.size() + ", constraints: " + constraints.size();
    }
}
