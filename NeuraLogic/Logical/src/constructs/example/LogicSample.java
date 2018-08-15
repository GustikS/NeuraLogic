package constructs.example;

import learning.LearningSample;
import networks.evaluation.values.Value;


/**
 * Represents highest level abstraction for supervised learning methods.
 * <p>
 * Created by gusta on 8.3.17.
 */
public class LogicSample extends LearningSample {

    public LogicSample(Value v, QueryAtom q) {
        this.query = q;
        this.target = v;

    }

}