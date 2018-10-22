package constructs;

import constructs.template.metadata.PredicateMetadata;
import ida.ilp.logic.Predicate;
import networks.structure.components.weights.Weight;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class WeightedPredicate {
    private static final Logger LOG = Logger.getLogger(WeightedPredicate.class.getName());

    /**
     * Offset (for disjunction activation) at the level of a template ultimately belongs to predicate!
     * todo check this with Ondra
     */
    @Nullable
    public Weight weight;

    /**
     * Regular logic predicate
     */
    public Predicate predicate;

    @Nullable
    PredicateMetadata metadata;

    public WeightedPredicate(Predicate predicate, Weight weight) {
        this.predicate = predicate;
        this.weight = weight;
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