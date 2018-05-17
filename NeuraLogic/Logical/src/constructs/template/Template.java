package constructs.template;

import constructs.Conjunction;
import constructs.example.ValuedFact;
import settings.Settings;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Gusta on 04.10.2016.
 */
public class Template {
    public LinkedHashSet<WeightedRule> rules;
    public LinkedHashSet<ValuedFact> facts;
    public LinkedHashSet<Conjunction> constraints;

    public Template() {
    }

    public Template(Set<WeightedRule> rules, Set<ValuedFact> facts) {
        this.rules = new LinkedHashSet<>(rules);
        this.facts = new LinkedHashSet<>(facts);
    }

    public Template preprocess(Settings settings) {
        Template preprocessed = this;
        if (settings.reduceTemplate) preprocessed = settings.templateReducer.reduce(preprocessed);
        return preprocessed;
    }
}
