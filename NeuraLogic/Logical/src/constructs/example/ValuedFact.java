package constructs.example;

import constructs.Atom;
import constructs.WeightedPredicate;
import ida.ilp.logic.Term;
import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gusta on 13.3.17.
 * <p>
 * Fact with a corresponding truth value
 */
public class ValuedFact extends Atom {

    private static final Logger LOG = Logger.getLogger(Atom.class.getName());
    public Weight weight;

    public ValuedFact(WeightedPredicate weightedPredicate, List<Term> terms, boolean negated, Weight weight) {
        super(weightedPredicate, terms, negated);
        if (weight != null) {
            this.weight = weight;
            this.setValue(weight.value);
        }
    }

    public Weight getOffset() {
        return offsettedPredicate.weight;
    }

    public Value getValue() {
        if (weight != null)
            return weight.value;
        return null;
    }

    public void setValue(Value value) {
        if (weight != null)
            this.weight.value = value;
        else {
            LOG.warning("Setting a ValuedFact value without a weight");
            this.weight = new Weight(-1, "foo", value, true, true);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (weight != null) {
            sb.append("w:").append(weight.toString()).append(" : ");
        }
        sb.append(super.toString());
        if (getOffset() != null) {
            sb.append(getOffset().toString());
        }
        return sb.toString();
    }
}