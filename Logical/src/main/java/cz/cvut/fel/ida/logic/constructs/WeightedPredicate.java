package cz.cvut.fel.ida.logic.constructs;

import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.Predicate;
import cz.cvut.fel.ida.logic.constructs.template.metadata.PredicateMetadata;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.logging.Logger;

public class WeightedPredicate implements Serializable {
    private static final Logger LOG = Logger.getLogger(WeightedPredicate.class.getName());

    /**
     * Offset (for disjunction activation) at the level of a template ultimately belongs to predicate!
     * todo check this with Ondra
     */
    @Nullable
    public Weight weight;

    /**
     * If it has the weight, it should be carrying the activation too.
     */
    @Nullable
    public Transformation transformation;

    @Nullable
    public Combination combination;

    /**
     * Regular logic predicate
     */
    public Predicate predicate;

    @Nullable
    public
    PredicateMetadata metadata;

    public WeightedPredicate(Predicate predicate, Weight weight) {
        this.predicate = predicate;
        this.weight = weight;
    }

    public static WeightedPredicate construct(String name, int arity, Boolean special, Boolean hidden) {
        Predicate predicate = Predicate.construct(name, arity, special, hidden);
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