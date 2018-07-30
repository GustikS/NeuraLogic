package constructs.example;

import constructs.template.Atom;
import constructs.template.Template;
import learning.Model;
import learning.Query;
import networks.evaluation.values.Value;

import java.util.Optional;

/**
 * Created by Gusta on 04.10.2016.
 */
public class AtomQuery implements Query {
    Atom query;
    Optional<LiftedExample> evidence;

    public AtomQuery(Atom query, LiftedExample evidence){
        this.query = query;
        this.evidence = Optional.ofNullable(evidence);
    }

    @Override
    public Optional<LiftedExample> getEvidence() {
        return evidence;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value evaluate(Model model) {
        if (model instanceof Template){
            return evaluate((Template) model);
        } else {
            return null;
        }
    }

    public Value evaluate(Template template) {
        return null;
    }


    public static AtomQuery parse(String s){
        return null;
    }
}
