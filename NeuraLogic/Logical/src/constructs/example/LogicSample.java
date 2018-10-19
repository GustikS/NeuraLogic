package constructs.example;

import learning.LearningSample;
import networks.computation.values.Value;


/**
 *
 * Created by gusta on 8.3.17.
 */
public class LogicSample extends LearningSample<QueryAtom> {

    public LogicSample(Value v, QueryAtom q) {
        this.query = q;
        this.target = v;

    }

}