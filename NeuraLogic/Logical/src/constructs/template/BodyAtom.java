package constructs.template;

import constructs.WeightedPredicate;
import networks.evaluation.functions.Activation;
import networks.structure.Weight;

/**
 * Created by gusta on 13.3.17.
 */
public class BodyAtom {
    public ida.ilp.logic.Literal literal;

    public WeightedPredicate weightedPredicate;
    public Weight weight;

    public boolean isNegated;
    Activation negation;
    public String originalString;

}