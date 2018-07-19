package constructs.template;

import constructs.Conjunction;
import constructs.example.ValuedFact;
import constructs.template.templates.ParsedTemplate;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by Gusta on 04.10.2016.
 */
public class Template extends ParsedTemplate {
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

}
