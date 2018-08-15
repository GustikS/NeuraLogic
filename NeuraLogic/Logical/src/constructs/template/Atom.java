package constructs.template;

import constructs.WeightedPredicate;
import ida.ilp.logic.Literal;
import networks.evaluation.functions.Activation;
import org.jetbrains.annotations.Nullable;

/**
 * Created by gusta on 8.3.17.
 */
public class Atom {
    public Literal literal;

    /**
     * The predicate carries the disjunction offset weight.
     * Disjunction's offset needs to be explicit since adding a disjunctive "offset literal" would change the semantics (would force the head to be always true)
     */
    public WeightedPredicate weightedPredicate;

    @Nullable
    Activation activation;
    public String originalString;

    public Atom(){

    }

    public Atom(Literal literal, WeightedPredicate weightedPredicate) {
        this.literal = literal;
        this.weightedPredicate = weightedPredicate;
    }
}