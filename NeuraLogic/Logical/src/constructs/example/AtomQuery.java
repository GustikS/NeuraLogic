package constructs.example;

import constructs.template.Atom;
import learning.Example;
import learning.Query;
import networks.evaluation.values.Value;

/**
 * Created by Gusta on 04.10.2016.
 */
public class AtomQuery implements Query {
    Atom query;
    GroundExample example;

    public AtomQuery(Atom query, GroundExample example){
        this.query = query;
        this.example = example;
    }

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

    public static AtomQuery parse(String s){
        return null;
    }
}