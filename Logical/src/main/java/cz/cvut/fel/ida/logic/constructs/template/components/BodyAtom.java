package cz.cvut.fel.ida.logic.constructs.template.components;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
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

    public ElementWise softNegation;

    /**
     * Whether to interpret negated literals with hard negation or fuzzy negation.
     * hardNegation - will create negated literals and send it correspondingly to the logical engine
     * softNegation - will only use the negation as a flag to apply corresponding negation activation function to the body atom
     */
    public BodyAtom(WeightedPredicate weightedPredicate, List<Term> terms, boolean hardNegation, ElementWise softNegation, Weight weight) {
        super(weightedPredicate, terms, hardNegation);
        this.weight = weight;
        this.softNegation = softNegation;
    }

    public BodyAtom(BodyAtom bodyAtom) {
        super(bodyAtom);
    }

    public boolean isNegated() {
        if (softNegation != null)
            return true;
        return super.isNegated();
    }

    public ElementWise getNegationActivation() {
        return softNegation;
    }

    public void setNegationActivation(ElementWise softNegation){
        this.softNegation = softNegation;
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