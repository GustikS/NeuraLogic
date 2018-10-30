package constructs.example;

import constructs.template.components.HeadAtom;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.Query;
import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.values.Value;

import java.util.List;
import java.util.Optional;

/**
 * Created by gusta on 13.3.17.
 */
@Deprecated
public class CompositeQuery implements Query{
    /**
     * list of query atoms with possible negations
     */
    List<Pair<HeadAtom,Activation>> queryAtoms;

    Activation aggregationFcn;
    Activation activationFcn;

    @Override
    public Optional<LiftedExample> getEvidence() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value evaluate(Template template) {
        return null;
    }

    public static CompositeQuery parse(String s){
        return null;
    }
}