package constructs.example;

import constructs.template.Template;
import constructs.template.components.HeadAtom;
import learning.Query;
import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.values.Value;
import settings.Settings;
import utils.generic.Pair;

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
        super(id, queryCounter, importance);
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