package constructs.template;

import networks.evaluation.functions.Activation;
import networks.structure.Weight;

/**
 * Created by gusta on 13.3.17.
 */
public class BodyAtom {
    Weight weight;
    Atom atom;
    Activation negation;    //may be null (or identity function) if not negated

    public boolean isNegated() {
        return negation != null;
    }
}