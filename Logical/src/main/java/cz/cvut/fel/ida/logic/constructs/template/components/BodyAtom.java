package cz.cvut.fel.ida.logic.constructs.template.components;

import cz.cvut.fel.ida.algebra.functions.Activation;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.constructs.Atom;
import cz.cvut.fel.ida.logic.constructs.WeightedPredicate;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by gusta on 13.3.17.
 */
public class BodyAtom extends Atom {

    @Nullable
    protected Weight weight;

    public BodyAtom(WeightedPredicate weightedPredicate, List<Term> terms, boolean negated, Weight weight) {
        super(weightedPredicate, terms, negated);
        this.weight = weight;
    }

    public BodyAtom(BodyAtom bodyAtom) {
        super(bodyAtom);
    }

    public Activation getNegationActivation() {
        return activation;
    }

    public Weight getConjunctWeight() {
        return weight;
    }


    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        BodyAtom other = (BodyAtom) obj;
        if (weight != null)
            return weight.equals(other.weight);
        else
            return other.weight == null;
    }
}