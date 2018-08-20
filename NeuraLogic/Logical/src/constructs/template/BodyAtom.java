package constructs.template;

import constructs.Atom;
import constructs.WeightedPredicate;
import ida.ilp.logic.Term;
import networks.evaluation.functions.Activation;
import networks.structure.Weight;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    public Activation getNegationActivation() {
        return activation;
    }

    public Weight getConjunctWeight() {
        return weight;
    }
}