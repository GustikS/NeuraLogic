package constructs.example;

import constructs.template.Atom;
import constructs.template.Template;
import learning.Example;
import learning.Query;
import networks.evaluation.values.Value;

/**
 * Created by Gusta on 04.10.2016.
 */
public class AtomQuery implements Query {
    Atom query;
    GroundExample evidence;

    public AtomQuery(Atom query, GroundExample evidence){
        this.query = query;
        this.evidence = evidence;
    }

    @Override
    public Example getEvidence() {
        return evidence;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value evaluate(Template template) {
        return null;
    }


    public static AtomQuery parse(String s){
        return null;
    }
}
