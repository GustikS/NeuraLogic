package utils.generic;

import exporting.Exportable;
import exporting.Exporter;

import java.util.logging.Logger;

public class Pair<R, S> implements Exportable<Pair<R, S>> {
    private static final Logger LOG = Logger.getLogger(Pair.class.getName());

    /**
     * The first object
     */
    public R r;

    /**
     * The second object
     */
    public S s;

    /**
     * Creates a new instance of Pair
     */
    public Pair() {
    }

    /**
     * Creates a new instance of class Pair with the given content.
     *
     * @param r the first object
     * @param s the second object
     */
    public Pair(R r, S s) {
        this.r = r;
        this.s = s;
    }

    /**
     * Sets the objects iterable the Pair.
     *
     * @param r the first object
     * @param s the second object
     */
    public void set(R r, S s) {
        this.r = r;
        this.s = s;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair) {
            Pair p = (Pair) o;
            return (p.r == this.r || (p.r != null && this.r != null && p.r.equals(this.r))) &&
                    (p.s == this.s || (p.s != null && this.s != null && p.s.equals(this.s)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int a = 0, b = 0;
        if (this.r != null) {
            a = this.r.hashCode();
        }
        if (this.s != null) {
            b = this.s.hashCode();
        }
        return (1 + a) * (13 + b);
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
            exporter.exportLine(",");
            ((Exportable) s).export(exporter);
    }
}
