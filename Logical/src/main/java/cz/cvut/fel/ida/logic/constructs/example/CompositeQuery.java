package cz.cvut.fel.ida.logic.constructs.example;

import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.HeadAtom;
import cz.cvut.fel.ida.algebra.functions.Activation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.Query;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.List;

/**
 * Created by gusta on 13.3.17.
 */
@Deprecated
public class CompositeQuery extends Query<LiftedExample, Template> {
    /**
     * list of query atoms with possible negations
     */
    List<Pair<HeadAtom, Activation>> queryAtoms;

    Activation aggregationFcn;
    Activation activationFcn;

    public CompositeQuery(String id, int queryCounter, double importance, List<Pair<HeadAtom, Activation>> queryAtoms) {
        super(id, queryCounter, importance, null);
        this.queryAtoms = queryAtoms;
    }

    public static CompositeQuery parse(String s) {
        return null;
    }

    @Override
    public Value evaluate(Settings settings, Template model) {
        return null;
    }
}