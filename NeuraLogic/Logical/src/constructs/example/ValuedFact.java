package constructs.example;

import constructs.Atom;
import constructs.WeightedPredicate;
import ida.ilp.logic.Term;
import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;

import java.util.List;

/**
 * Created by gusta on 13.3.17.
 * <p>
 * Fact with a corresponding truth value
 */
public class ValuedFact extends Atom {

    private Weight weight;

    public ValuedFact(WeightedPredicate weightedPredicate, List<Term> terms, boolean negated, Weight weight) {
        super(weightedPredicate, terms, negated);
        this.weight = weight;
        this.setValue(weight.value);
    }

    public Weight getOffset() {
        return offsettedPredicate.weight;
    }

    public Value getFactValue() {
        return getValue();
    }

    public Value getValue() {
        return weight.value;
    }

    public void setValue(Value value) {
        this.weight.value = value;
    }
}