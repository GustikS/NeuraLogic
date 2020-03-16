package cz.cvut.fel.ida.logic.constructs.template.transforming;

import cz.cvut.fel.ida.logic.constructs.example.QueryAtom;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.types.GraphTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Deprecated
public class SupervisedReducer implements TemplateReducing {
    private static final Logger LOG = Logger.getLogger(SupervisedReducer.class.getName());

    Set<QueryAtom> queryAtomSet;

    public SupervisedReducer(Set<QueryAtom> queries){
        queryAtomSet = queries;
    }

    public SupervisedReducer(QueryAtom query){
        (queryAtomSet = new HashSet<>()).add(query);
    }

    @Override
    public Template reduce(Template itemplate) {
        return reduce(itemplate,queryAtomSet);
    }

    @Override
    public Template reduce(Template itemplate, QueryAtom query) {
        //TODO
        return null;
    }

    public Template reduce(Template itemplate, Set<QueryAtom> query) {
        //TODO
        return null;
    }

    public Template reduce(GraphTemplate itemplate) {
        return null;
    }
}
