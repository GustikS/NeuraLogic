package learning;

import evaluation.values.Value;
import networks.structure.building.Cachable;

public abstract class LearningSample<Q extends Query, C extends Cachable> {
    //should learning samples contain reference to Model - probably not (Structure learning)

    public Q query;
    public Value target;

    /**
     * For storing extra precomputed content for reuse, e.g. partial groundings or neuralizations
     */
    public C cache;

    public String getId(){
        return query.ID;
    }

    public double getImportance(){
        return query.importance;
    }

    @Override
    public String toString() {
        return getId() + ": " + target + "=" + query;
    }
}