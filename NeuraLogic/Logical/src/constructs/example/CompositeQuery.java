package constructs.example;

import constructs.template.Atom;
import ida.utils.tuples.Pair;
import learning.Example;
import learning.Query;
import networks.evaluation.functions.Activation;
import networks.evaluation.values.Value;

import java.util.List;

/**
 * Created by gusta on 13.3.17.
 */
public class CompositeQuery implements Query{
    /**
     * list of query atoms with possible negations
     */
    List<Pair<Atom,Activation>> queryAtoms;

    Activation aggregationFcn;
    Activation activationFcn;

    @Override
    public Example getExample() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value evaluate() {
        return null;
    }

    public static CompositeQuery parse(String s){
        return null;
    }
}