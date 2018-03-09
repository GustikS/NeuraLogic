package constructs.template;

import networks.evaluation.functions.Activation;

/**
 * Created by gusta on 8.3.17.
 */
public class Atom {
    public ida.ilp.logic.Literal literal;

    /**
     * The predicate carries the disjunction offset weight.
     * Disjunction's offset needs to be explicit since adding a disjunctive "offset literal" would change the semantics (would force the head to be always true)
     */
    public WeightedPredicate weightedPredicate;
    Activation activation;

    public String originalString;
}