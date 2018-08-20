package constructs;

import constructs.template.metadata.PredicateMetadata;
import ida.ilp.logic.Predicate;
import networks.structure.Weight;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class WeightedPredicate {
    private static final Logger LOG = Logger.getLogger(WeightedPredicate.class.getName());

    /**
     * Offset (for disjunction activation) at the level of a template utlimately belong to predicates
     */
    @Nullable
    public Weight offset;

    /**
     * Regular logic predicate
     */
    public Predicate predicate;

    @Nullable
    PredicateMetadata metadata;

    public WeightedPredicate(Predicate predicate, Weight offset) {
        this.predicate = predicate;
        this.offset = offset;
    }

    public static WeightedPredicate construct(String name, int arity, Boolean special) {
        Predicate predicate = Predicate.construct(name, arity, special);
        return new WeightedPredicate(predicate, null);
    }

    /**
     * Offsets are not visible by default
     *
     * @return
     */
    public String toString() {
        return predicate.toString();
    }
}