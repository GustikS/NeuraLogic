package constructs.example;

import constructs.WeightedPredicate;
import ida.ilp.logic.Literal;
import networks.structure.Weight;

/**
 * Created by gusta on 13.3.17.
 *
 * Fact with a corresponding truth value (not weight)
 */
public class ValuedFact {
    public Literal literal;
    public Weight value;
    public String originalString;
    public WeightedPredicate weightedPredicate;
    public boolean isNegated;
}

