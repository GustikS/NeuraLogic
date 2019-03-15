package constructs.example;

import learning.LearningSample;
import networks.computation.evaluation.values.Value;

import java.util.logging.Logger;


/**
 *
 * Created by gusta on 8.3.17.
 */
public class LogicSample extends LearningSample<QueryAtom> {
    private static final Logger LOG = Logger.getLogger(LogicSample.class.getName());

    public LogicSample(Value v, QueryAtom q) {
        this.query = q;
        this.target = v;
    }

    @Override
    public String toString() {
        return super.toString() + ", " + query.evidence;
    }
}