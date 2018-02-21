package constructs.template;

import ida.ilp.logic.Literal;
import networks.evaluation.functions.Activation;
import networks.structure.Weight;

/**
 * Created by gusta on 8.3.17.
 */
public class Atom {
    Literal literal;
    Activation activation;

    /**
     * //the disjunction's offset needs to be explicit since adding a disjunctive "offset literal" would change the semantics (would force the head to be always true)
     */
    Weight offset;

    public void setOffset(Weight offset) {
        this.offset = offset;
    }
}