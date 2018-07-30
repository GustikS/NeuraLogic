package constructs.template;

import constructs.Conjunction;
import constructs.example.ValuedFact;
import constructs.template.templates.ParsedTemplate;
import learning.Model;
import learning.Query;
import networks.evaluation.values.Value;
import networks.structure.Weight;
import training.NeuralModel;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by Gusta on 04.10.2016.
 */
public class Template extends ParsedTemplate implements Model {
    public LinkedHashSet<WeightedRule> rules;
    public LinkedHashSet<ValuedFact> facts;
    public LinkedHashSet<Conjunction> constraints;

    public Template() {
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
        return null;
    }

    @Override
    public <T extends Query> Value evaluate(T query) {
        return null;
    }

    @Override
    public List<Weight> getAllWeights() {
        return null;
    }

    public void updateWeightsFrom(NeuralModel neural) {
        //TODO
    }
}
