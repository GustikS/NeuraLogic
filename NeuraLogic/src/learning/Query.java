package learning;

import constructs.template.Template;
import networks.evaluation.values.Value;

/**
 * Query is like an output (Y) in supervised learning. It always connects to a particular example, just like we're given data in normal learning (Y->X).
 * It can be evaluated upn this example given some template (model).
 *
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
    Value evaluate(Template template);
}
