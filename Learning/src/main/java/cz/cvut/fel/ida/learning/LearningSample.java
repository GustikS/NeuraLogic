package cz.cvut.fel.ida.learning;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.utils.exporting.Exportable;

public abstract class LearningSample<Q extends Query, Object> implements Exportable {
    //should learning samples contain reference to Model? - probably not (Structure learning)

    public Q query;
    public Value target;

    /**
     * For storing extra precomputed content for reuse, e.g. partial groundings or neuralizations
     */
    public Object cache;

    /**
     * Use this learning sample only for validation (or training or testing)
     */
    public Split type = Split.UNKNOWN;

    public int position;

    public String getId() {
        return query.ID;
    }

    public double getImportance() {
        return query.importance;
    }

    @Override
    public String toString() {
        return getId() + ": " + target + "=" + query;
    }

    public enum Split {
        TRAIN, VALIDATION, TEST, UNKNOWN
    }
}
