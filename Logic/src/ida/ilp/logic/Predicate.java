package ida.ilp.logic;

import java.util.logging.Logger;

/**
 * Created by gusta on 1.3.18.
 */
public class Predicate {
    private static final Logger LOG = Logger.getLogger(Predicate.class.getName());

    public String name;
    public int arity;
    public boolean special;
    private static CharSequence specialSign = "@";

    public Predicate() {
    }

    public Predicate(String predicate, int arity) {
        this.name = predicate.intern();
        this.arity = arity;
    }

    public Predicate(String from) {
        if (from.contains("/")) {
            String[] split = from.split("/");
            name = split[0].intern();
            try {
                arity = Integer.parseInt(split[1]);
            } catch (Exception ex) {
                LOG.severe("Cannot parse predicate arity");
            }
        } else {
            name = from.intern();
        }
    }

    public Predicate(Literal literal) {
        this.name = literal.predicateName();
        this.arity = literal.arity();
    }

    public static Predicate construct(String name, int arity, Boolean special) {
        //TODO factory method with weak cache
        Predicate predicate = new Predicate();
        predicate.name = name.intern();
        predicate.arity = arity;
        predicate.special = special != null ? special : false;
        return predicate;
    }

    @Override
    public String toString() {
        return name + "/" + arity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Predicate) {
            Predicate p = (Predicate) obj;
            if (p.name.equals(this.name) && p.arity == this.arity) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return name.hashCode() * arity;
    }
}