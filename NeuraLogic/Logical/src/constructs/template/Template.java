package constructs.template;

import constructs.example.WeightedFact;
import settings.Settings;

import java.util.Set;

/**
 * Created by Gusta on 04.10.2016.
 */
public class Template {
    Set<WeightedRule> rules;
    Set<WeightedFact> facts;

    Set<Atom> atoms;

    public Template(Set<WeightedRule> rules, Set<WeightedFact> facts, Set<Atom> atoms) {
        this.rules = rules;
        this.facts = facts;
        this.atoms = atoms;
    }

    public Template preprocess(Settings settings){
        Template preprocessed = this;
        if (settings.reduceTemplate) preprocessed = settings.templateReducer.reduce(preprocessed);
        return preprocessed;
    }
}
