package cz.cvut.fel.ida.logic.constructs.template.components;

import cz.cvut.fel.ida.logic.constructs.Atom;
import cz.cvut.fel.ida.logic.constructs.WeightedPredicate;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.algebra.weights.Weight;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class HeadAtom extends Atom {

    private static final Logger LOG = Logger.getLogger(HeadAtom.class.getName());

    public boolean hasSomeWeightedRule = false;

    public HeadAtom(WeightedPredicate weightedPredicate, List<Term> terms) {
        super(weightedPredicate, terms, false);
    }

    public HeadAtom(Atom another) {
        super(another);
    }

    public Weight getOffset() {
        return offsettedPredicate.weight;
    }

}