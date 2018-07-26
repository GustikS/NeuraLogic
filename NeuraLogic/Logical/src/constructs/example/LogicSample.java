package constructs.example;

import learning.Example;
import learning.LearningSample;
import learning.Model;
import learning.Query;
import networks.evaluation.values.Value;


/**
 * Represents highest level abstraction for supervised learning methods.
 *
 * Created by gusta on 8.3.17.
 */
public class LogicSample implements LearningSample{
    double importance;
    AtomQuery query;
    Value target;

    public LogicSample(AtomQuery q, Value v){

    }

    public LogicSample(AtomQuery query, Example example) {
    }

    @Override
    public Double getImportance() {
        return importance;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value getTarget() {
        return target;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public Value getValue(Model model) {
        return model.evaluate(query);
    }

}