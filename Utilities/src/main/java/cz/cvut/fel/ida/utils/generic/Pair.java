package cz.cvut.fel.ida.utils.generic;

import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.exporting.Exporter;

import java.util.logging.Logger;

public class Pair<R, S> extends cz.cvut.fel.ida.utils.generic.tuples.Pair<R, S> implements Exportable<Pair<R, S>> {
    private static final Logger LOG = Logger.getLogger(Pair.class.getName());

    /**
     * Creates a new instance of class Pair with the given content.
     *
     * @param r the first object
     * @param s the second object
     */
    public Pair(R r, S s) {
        super(r, s);
    }


    @Override
    public String toString() {
        return "[" + r + ", " + s + "]";
    }

    @Override
    public void export(Exporter exporter) {
        if (r instanceof Exportable)
            ((Exportable) r).export(exporter);
        if (s instanceof Exportable)
            exporter.delimitNext();
        ((Exportable) s).export(exporter);
    }
}
