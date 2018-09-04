package constructs.example;

import constructs.template.HeadAtom;
import constructs.template.Template;
import learning.Query;
import networks.evaluation.values.Value;

/**
 * Created by Gusta on 04.10.2016.
 *
 * HeadAtom desont have to be ground! - it will apply to all compatible ground neurons then
 */
public class QueryAtom extends Query<LiftedExample, Template> {

    public HeadAtom headAtom;

    public QueryAtom(String id, int queryCounter, double importance, HeadAtom query, LiftedExample evidence) {
        super(id, queryCounter, importance);
        this.headAtom = query;
        this.evidence = evidence;
    }

    public QueryAtom(String id, int queryCounter, double importance, HeadAtom query) {
        super(id, queryCounter, importance);
    }


    @Override
    public Value evaluate(Template template) {
        return null;
    }

}