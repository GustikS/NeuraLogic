package constructs;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import ida.ilp.logic.Literal;
import ida.ilp.logic.Predicate;
import ida.ilp.logic.Term;
import networks.computation.evaluation.functions.Activation;

import java.util.List;
import java.util.logging.Logger;

public class Atom {
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
    public Activation activation;
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
        this.activation = offsettedPredicate.activation;    //by default we inhering the activation from the predicate
    }

    public Atom(Atom another) {
        this.offsettedPredicate = another.offsettedPredicate;
        this.literal = another.literal;
        this.activation = another.activation;
        this.originalString = another.originalString;
    }

    public Predicate getPredicate() {
        return literal.predicate();
    }

    public WeightedPredicate getOffsettedPredicate() {
        return offsettedPredicate;
    }

    public boolean isNegated() {
        return literal.isNegated();
    }

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

    public Activation getActivation() {
        if (this.activation != null)
            return this.activation;
        else if (this.offsettedPredicate.activation != null)
            return this.offsettedPredicate.activation;
        else
            return null;
    }
}