package constructs.template;

import constructs.Conjunction;
import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import learning.Model;
import networks.evaluation.values.Value;
import networks.structure.Weight;
import training.NeuralModel;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by Gusta on 04.10.2016.
 */
public class Template implements Model<QueryAtom> {
    String id;

    public LinkedHashSet<WeightedRule> rules;
    public LinkedHashSet<ValuedFact> facts;
    public LinkedHashSet<Conjunction> constraints;  //todo how to handle these?

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

    public void addConstraints(List<Conjunction> constr){
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

    public void buildGraph() {
    }
}
