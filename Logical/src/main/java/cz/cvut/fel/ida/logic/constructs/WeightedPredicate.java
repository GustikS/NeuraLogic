package cz.cvut.fel.ida.logic.constructs;

import com.sun.istack.internal.Nullable;
import cz.cvut.fel.ida.algebra.functions.Activation;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.Predicate;
import cz.cvut.fel.ida.logic.constructs.template.metadata.PredicateMetadata;

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
     * If it has the weight, it shouldbe carrying the activation too.
     */
    @Nullable
    public Activation activation;

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