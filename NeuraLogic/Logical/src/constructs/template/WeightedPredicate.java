package constructs.template;

import ida.ilp.logic.Predicate;
import networks.structure.Weight;

import java.util.logging.Logger;

public class WeightedPredicate {
    private static final Logger LOG = Logger.getLogger(WeightedPredicate.class.getName());

    /**
     * Offset (for disjunction activation) at the level of a template utlimately belong to predicates
     */
    public Weight offset;

    /**
     * Regular logic predicate
     */
    public Predicate predicate;

    public static WeightedPredicate construct(String from, int arity, boolean special) {
        WeightedPredicate wp = new WeightedPredicate();
        wp.predicate = Predicate.construct(from, arity, special);
        wp.offset = null;
        return wp;
    }

    /**
     * Offsets are not visible by default
     * @return
     */
    public String toString(){
        return predicate.toString();
    }
}