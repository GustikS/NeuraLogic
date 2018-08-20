package constructs.example;

import constructs.Atom;
import constructs.WeightedPredicate;
import ida.ilp.logic.Term;
import networks.evaluation.values.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by gusta on 13.3.17.
 * <p>
 * Fact with a corresponding truth value (not weight, but may be sharable)
 */
public class ValuedFact extends Atom {

    @NotNull
    protected Value value;

    public ValuedFact(WeightedPredicate weightedPredicate, List<Term> terms, boolean negated, Value value) {
        super(weightedPredicate, terms, negated);
        this.value = value;
    }

    public Value getFactValue() {
        return value;
    }
}