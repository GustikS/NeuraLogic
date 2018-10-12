package constructs.template;

import constructs.Conjunction;
import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.types.GraphTemplate;
import grounding.bottomUp.HerbrandModel;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.Predicate;
import ida.utils.collections.MultiMap;
import learning.Model;
import networks.evaluation.values.Value;
import networks.structure.weights.Weight;
import org.jetbrains.annotations.Nullable;
import training.NeuralModel;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Gusta on 04.10.2016.
 */
public class Template implements Model<QueryAtom> {
    private static final Logger LOG = Logger.getLogger(Template.class.getName());
    String id;

    public LinkedHashSet<WeightedRule> rules;

    public LinkedHashSet<ValuedFact> facts;
    public LinkedHashSet<Conjunction> constraints;  //todo how to handle these?

    @Nullable
    Set<Literal> inferredLiterals;

    public Template() {
    }

    public Template(Template other) {
        this.rules = other.rules;
        this.facts = other.facts;
        this.constraints = other.constraints;
    }

    public Template(List<WeightedRule> rules, List<ValuedFact> facts) {
        this.rules = new LinkedHashSet<>(rules);
        this.facts = new LinkedHashSet<>(facts);
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
        return null;
    }

    public void updateWeightsFrom(NeuralModel neural) {
        //TODO
    }

    public LinkedHashSet<ValuedFact> getValuedFacts() {
        return facts;
    }

    public Set<Literal> getAllFacts() {
        HashSet<Literal> literals = new HashSet<>();
        literals.addAll(inferTemplateFacts());
        literals.addAll(facts.stream().map(ValuedFact::getLiteral).collect(Collectors.toList()));
        return literals;
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
        if (template == this){
            return;
        }
        rules.addAll(template.rules);
        facts.addAll(template.facts);
        constraints.addAll(template.constraints);
    }
}
