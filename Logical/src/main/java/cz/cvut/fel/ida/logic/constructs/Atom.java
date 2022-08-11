package cz.cvut.fel.ida.logic.constructs;

import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Predicate;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Logger;

public class Atom implements Exportable {
    private static final Logger LOG = Logger.getLogger(Atom.class.getName());

    /**
     * The predicates themselves ultimately carry the disjunction offset weight.
     * Disjunction's offset needs to be explicit since adding a disjunctive "weight literal" would change the semantics (would force the head to be always true)
     */
    @NotNull
    public WeightedPredicate offsettedPredicate;

    @NotNull
    public Literal literal;

    @Nullable
    public Combination combination;

    @Nullable
    public Transformation transformation;

    @Nullable
    public String originalString;

    /**
     * To be set before grounding - todo
     */
    public boolean dropout;

    public Atom(WeightedPredicate weightedPredicate, List<Term> terms, boolean negated) {
        if (weightedPredicate.predicate.arity != terms.size()) {
            LOG.severe("Predicate arity and terms size mismatch while creating an Atom");   //tried some workarounds with injecting predicate factory, but this probably has to be here (since predicate and terms are created at separate places)
        }
        this.offsettedPredicate = weightedPredicate;
        this.literal = new Literal(weightedPredicate.predicate, negated, terms);
        this.combination = weightedPredicate.combination;
        this.transformation = weightedPredicate.transformation;    //by default we are inheriting the activation from the predicate
    }

    public Atom(Atom another) {
        this.offsettedPredicate = another.offsettedPredicate;
        this.literal = another.literal;
        this.combination = another.combination;
        this.transformation = another.transformation;
        this.originalString = another.originalString;
        this.dropout = another.dropout;
    }

    public Predicate getPredicate() {
        return literal.predicate();
    }

    @NotNull
    public WeightedPredicate getOffsettedPredicate() {
        return offsettedPredicate;
    }

    public boolean isNegated() {
        return literal.isNegated();
    }

    @NotNull
    public Literal getLiteral() {
        return literal;
    }


    @Override
    public int hashCode() {
        return literal.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Atom)) {
            return false;
        }
        Atom other = (Atom) obj;
        return literal.equals(other.literal);
    }

    @Override
    public String toString() {
        return literal.toString();
    }

    public Transformation getTransformation() {
        if (this.transformation != null)
            return this.transformation;
        else if (this.offsettedPredicate.transformation != null)
            return this.offsettedPredicate.transformation;
        else
            return null;
    }

    public Combination getCombination() {
        if (this.combination != null)
            return this.combination;
        else if (this.offsettedPredicate.combination != null)
            return this.offsettedPredicate.combination;
        else
            return null;
    }
}