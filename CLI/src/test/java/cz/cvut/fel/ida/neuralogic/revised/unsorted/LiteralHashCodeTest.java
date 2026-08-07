package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.logic.Constant;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Literal caches its hashCode, so every mutation has to invalidate that cache. HerbrandModel.TupleNotIn, the
 * custom predicate behind stratified negation, reuses a single Literal as a lookup key through setTerms - if the
 * cache survives, every lookup after the first probes the wrong bucket, the negation stops pruning, and the
 * Herbrand model silently gains atoms that do not follow.
 */
public class LiteralHashCodeTest {
    private static final Logger LOG = Logger.getLogger(LiteralHashCodeTest.class.getName());

    @TestAnnotations.Fast
    public void reusedLiteralIsFoundAfterItsTermsChange() {
        Literal a = new Literal("edge", terms("a1", "a2"));
        Literal b = new Literal("edge", terms("b1", "b2"));

        Set<Literal> known = new HashSet<>();
        known.add(b);

        Literal key = new Literal("edge", terms("a1", "a2"));
        key.hashCode();                     // the first lookup caches the hash, as TupleNotIn's first call does
        assertFalse(known.contains(key));

        key.setTerms(b.termList().toArray(new Term[0]));
        assertTrue(known.contains(key), "a reused key must be found once its terms match");

        key.setTerms(a.termList().toArray(new Term[0]));
        assertFalse(known.contains(key));
    }

    private static Term[] terms(String... names) {
        Term[] terms = new Term[names.length];
        for (int i = 0; i < names.length; i++) {
            terms[i] = Constant.construct(names[i]);
        }
        return terms;
    }
}
