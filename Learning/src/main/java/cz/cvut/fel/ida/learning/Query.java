package cz.cvut.fel.ida.learning;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import org.jetbrains.annotations.Nullable;

/**
 * Query is like an output (Y) in supervised learning. It connects to a particular example, just like we're given data in normal learning (Y<-X).
 * The example data may also be hidden within the model. It can be evaluated given some template (model).
 * <p>
 * Created by Gusta on 04.10.2016.
 */
public abstract class Query<E extends Example, M extends Model> implements Exportable {

    /**
     * Some relative position within context of a builder (a train/test set (fold)) before shuffling
     */
    public int position;

    public Query(String id, int queryCounter, double importance, E evidence) {
        this.ID = id;
        this.position = queryCounter;
        this.importance = importance;
        this.evidence = evidence;
    }

    /**
     * Returns a particular example (logic clauses OR neural network) that this query corresponds to
     *
     * @return
     */
    @Nullable
    public E evidence;

    /**
     * Identifies this query, i.e. logical atom or neural network output
     *
     * @return
     */
    public String ID;

    /**
     * Weight of this query for learning/evaluation
     */
    public double importance;

    protected Query() {
        //do not use (for serialization only)
    }

    /**
     * Return an getValue result of this query, this might be boolean (for logical query) or any numerical value
     *
     * @return
     */
    public abstract Value evaluate(Settings settings, M model);
}