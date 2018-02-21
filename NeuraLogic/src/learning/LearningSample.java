package learning;

import networks.evaluation.values.Value;

/**
 * Created by gusta on 8.3.17.
 */
public class LearningSample {
    double importance;
    Query query;
    Value target;

    public LearningSample(Query q, Value v){

    }

    public LearningSample(Query query, Example example) {
    }
}