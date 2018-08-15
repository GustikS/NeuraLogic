package constructs.example;

import constructs.template.Atom;
import constructs.template.Template;
import learning.Query;
import networks.evaluation.values.Value;

/**
 * Created by Gusta on 04.10.2016.
 */
public class QueryAtom extends Query<LiftedExample, Template> {

    Atom atom;

    public QueryAtom(String id, int queryCounter, double importance, Atom query, LiftedExample evidence) {
        super(id, queryCounter, importance);
        this.atom = query;
        this.evidence = evidence;
    }

    public QueryAtom(String id, int queryCounter, double importance, Atom query) {
        super(id, queryCounter, importance);
    }


    @Override
    public Value evaluate(Template template) {
        return null;
    }

}