package constructs;

import ida.ilp.logic.Literal;
import ida.ilp.logic.Predicate;
import ida.ilp.logic.Term;
import networks.evaluation.functions.Activation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Logger;

public class Atom {
    private static final Logger LOG = Logger.getLogger(Atom.class.getName());

    /**
     * The predicates themselves ultimately carry the disjunction offset weight.
     * Disjunction's offset needs to be explicit since adding a disjunctive "offset literal" would change the semantics (would force the head to be always true)
     */
    @NotNull
    protected WeightedPredicate offsettedPredicate;

    @NotNull
    protected Literal literal;

    @Nullable
    protected Activation activation;
    @Nullable
    public String originalString;

    public Atom(WeightedPredicate weightedPredicate, List<Term> terms, boolean negated) {
        if (weightedPredicate.predicate.arity != terms.size()){
            LOG.severe("Predicate arity and terms size mismatch while creating an Atom");   //tried some workarounds with injecting predicate factory, but this probably has to be here (since predicate and terms are created at separate places)
        }
        this.offsettedPredicate = weightedPredicate;
        this.literal = new Literal(weightedPredicate.predicate, negated, terms);
    }

    public Atom(Atom another){
        this.offsettedPredicate = another.offsettedPredicate;
        this.literal = another.literal;
        this.activation = another.activation;
        this.originalString = another.originalString;
    }

    public Predicate getPredicate() {
        return literal.predicate();
    }

    public boolean isNegated() {
        return literal.isNegated();
    }

    public Literal getLiteral() {
        return literal;
    }
}