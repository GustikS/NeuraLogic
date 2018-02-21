package learning;

import networks.evaluation.values.Value;

/**
 * Created by Gusta on 04.10.2016.
 */
public interface Query {
    /**
     * Returns a particular example that this query corresponds to
     * @return
     */
    Example getExample();
    /**
     * Uniquely identifies this query, i.e. logical atom or neural network output
     * @return
     */
    String getId();

    /**
     * Return an evaluate result of this query, this might be boolean (for logical query) or any numerical value
     * @return
     */
    Value evaluate();
}
