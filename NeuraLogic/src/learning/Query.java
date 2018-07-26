package learning;

import networks.evaluation.values.Value;

/**
 * Query is like an output (Y) in supervised learning. It always connects to a particular example, just like we're given data in normal learning (Y->X).
 * Without the context of example it makes no sense. It can be evaluated upon this example given some template (model).
 *
 * Created by Gusta on 04.10.2016.
 */
public interface Query {
    /**
     * Returns a particular example (logic clauses OR neural network) that this query corresponds to
     * @return
     */
    Example getEvidence();
    /**
     * Uniquely identifies this query, i.e. logical atom or neural network output
     * @return
     */
    String getId();

    /**
     * Return an getValue result of this query, this might be boolean (for logical query) or any numerical value
     * @return
     */
    Value evaluate(Model model);
}
