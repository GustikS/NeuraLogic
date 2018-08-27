package constructs.template;

import constructs.Atom;
import constructs.WeightedPredicate;
import ida.ilp.logic.Term;
import networks.evaluation.functions.Activation;
import networks.structure.Weight;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by gusta on 13.3.17.
 */
public class BodyAtom extends Atom {

    @NotNull
    protected Weight weight;

    public BodyAtom(WeightedPredicate weightedPredicate, List<Term> terms, boolean negated, Weight weight) {
        super(weightedPredicate,  terms, negated);
        this.weight = weight;
    }

    public BodyAtom(BodyAtom bodyAtom) {
        super(bodyAtom);
    }

    public Activation getNegationActivation() {
        return activation;
    }

    public Weight getConjunctWeight() {
        return weight;
    }

    @Override
    public BodyAtom ground(Map<Term,Term> var2term) {
        BodyAtom copy = new BodyAtom(this);
        copy.literal = copy.literal.subsCopy(var2term);
        return copy;
    }
}