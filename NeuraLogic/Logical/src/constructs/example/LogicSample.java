package constructs.example;

import evaluation.values.Value;
import learning.LearningSample;
import networks.structure.building.Cachable;

import java.util.logging.Logger;


/**
 * Created by gusta on 8.3.17.
 */
public class LogicSample extends LearningSample<QueryAtom, Cachable> {
    private static final Logger LOG = Logger.getLogger(LogicSample.class.getName());

    public LogicSample(Value v, QueryAtom q) {
        this.query = q;
        this.target = v;
    }

    public static int compare(LogicSample o1, LogicSample o2) {
        if (o1.query.position < o2.query.position) {
            return -1;
        }
        if (o1.query.position > o2.query.position) {
            return 1;
        }
        return 0;
    }

    public String getQueryId(){
        return super.getId();
    }
}