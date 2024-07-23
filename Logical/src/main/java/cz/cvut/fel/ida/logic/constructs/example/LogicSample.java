package cz.cvut.fel.ida.logic.constructs.example;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.LearningSample;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


/**
 * Created by gusta on 8.3.17.
 */
public class LogicSample extends LearningSample<QueryAtom, Object> implements Exportable {
    private static final Logger LOG = Logger.getLogger(LogicSample.class.getName());

    public boolean isQueryOnly;

    public LogicSample(Value v, QueryAtom q) {
        this.query = q;
        this.target = v;
        this.position = q.position;
    }

    public LogicSample(Value v, QueryAtom q, boolean isQueryOnly) {
        this.query = q;
        this.target = v;
        this.position = q.position;
        this.isQueryOnly = isQueryOnly;
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

    public String getQueryId() {
        return super.getId();
    }
}