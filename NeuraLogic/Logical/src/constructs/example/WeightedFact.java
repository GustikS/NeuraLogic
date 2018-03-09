package constructs.example;

import constructs.template.WeightedPredicate;
import ida.ilp.logic.Literal;
import networks.structure.Weight;

/**
 * Created by gusta on 13.3.17.
 */
public class WeightedFact {
    public Literal literal;
    public Weight value;
    public String originalString;
    public WeightedPredicate weightedPredicate;
}
