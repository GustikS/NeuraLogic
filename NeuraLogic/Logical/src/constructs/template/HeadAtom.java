package constructs.template;

import constructs.Atom;
import constructs.WeightedPredicate;
import ida.ilp.logic.Term;
import networks.structure.Weight;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class HeadAtom extends Atom {

    private static final Logger LOG = Logger.getLogger(HeadAtom.class.getName());


    public HeadAtom(WeightedPredicate weightedPredicate, List<Term> terms) {
        super(weightedPredicate, terms, false);
    }

    public HeadAtom(Atom another){
        super(another);
    }

    public Weight getOffset() {
        return offsettedPredicate.weight;
    }

    @Override
    public HeadAtom ground(Map<Term,Term> var2term) {
        HeadAtom copy = new HeadAtom(this);
        copy.literal = copy.literal.subsCopy(var2term);
        return copy;
    }
}