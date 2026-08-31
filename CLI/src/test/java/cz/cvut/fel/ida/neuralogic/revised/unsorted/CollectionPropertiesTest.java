package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.math.collections.IntegerSet;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;
import cz.cvut.fel.ida.utils.math.collections.ValueToIndex;

import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The hand-written collections underneath the grounder were rewritten heavily for performance and have no
 * tests of their own, so this checks them differentially against java.util on random input rather than by
 * reading them. IntegerSet in particular picks between four union strategies depending on the sizes and the
 * overlap of its inputs, which is exactly where an edge case hides.
 */
public class CollectionPropertiesTest {
    private static final Logger LOG = Logger.getLogger(CollectionPropertiesTest.class.getName());

    @TestAnnotations.Fast
    public void matchReferenceImplementations() {
        Random random = new Random(42);
        int failures = 0;

        for (int round = 0; round < 4000; round++) {
            TreeSet<Integer> refA = randomSet(random);
            TreeSet<Integer> refB = randomSet(random);
            IntegerSet a = IntegerSet.createIntegerSet(new HashSet<>(refA));
            IntegerSet b = IntegerSet.createIntegerSet(new HashSet<>(refB));

            failures += check("union", union(refA, refB), IntegerSet.union(a, b), round);
            failures += check("intersection", intersect(refA, refB), IntegerSet.intersection(a, b), round);
            failures += check("difference", difference(refA, refB), IntegerSet.difference(a, b), round);

            if (!refA.isEmpty()) {
                if (a.min() != refA.first() || a.max() != refA.last()) {
                    LOG.severe("min/max mismatch at round " + round);
                    failures++;
                }
            }
        }

        for (int round = 0; round < 2000; round++) {
            ValueToIndex<String> vti = new ValueToIndex<>();
            Map<String, Integer> refForward = new HashMap<>();
            Map<Integer, String> refBackward = new HashMap<>();
            for (int i = 0; i < 40; i++) {
                String value = "v" + random.nextInt(25);
                int index = vti.valueToIndex(value);
                Integer known = refForward.get(value);
                if (known == null) {
                    refForward.put(value, index);
                    refBackward.put(index, value);
                } else if (known != index) {
                    LOG.severe("ValueToIndex gave two indices for " + value + " at round " + round);
                    failures++;
                }
            }
            for (Map.Entry<String, Integer> entry : refForward.entrySet()) {
                if (!Objects.equals(vti.indexToValue(entry.getValue()), entry.getKey())
                        || vti.getIndex(entry.getKey()) != entry.getValue()
                        || !vti.containsValue(entry.getKey())) {
                    LOG.severe("ValueToIndex round trip broken at round " + round);
                    failures++;
                }
            }
            if (vti.size() != refForward.size()) {
                LOG.severe("ValueToIndex size " + vti.size() + " vs " + refForward.size());
                failures++;
            }
        }

        for (int round = 0; round < 2000; round++) {
            MultiMap<String, Integer> mm = new MultiMap<>();
            Map<String, Set<Integer>> ref = new HashMap<>();
            for (int i = 0; i < 30; i++) {
                String key = "k" + random.nextInt(8);
                int value = random.nextInt(20);
                mm.put(key, value);
                ref.computeIfAbsent(key, k -> new HashSet<>()).add(value);
            }
            for (Map.Entry<String, Set<Integer>> entry : ref.entrySet()) {
                if (!new HashSet<>(mm.get(entry.getKey())).equals(entry.getValue())) {
                    LOG.severe("MultiMap mismatch for " + entry.getKey() + " at round " + round);
                    failures++;
                }
            }
            if (!mm.get("absent-key").isEmpty()) {
                LOG.severe("MultiMap missing key not empty at round " + round);
                failures++;
            }
        }

        assertEquals(0, failures, "collections disagreed with the java.util reference");
    }

    private static TreeSet<Integer> randomSet(Random random) {
        TreeSet<Integer> set = new TreeSet<>();
        int size = random.nextInt(40);
        for (int i = 0; i < size; i++) {
            set.add(random.nextInt(120) - 60);
        }
        return set;
    }

    private static TreeSet<Integer> union(TreeSet<Integer> a, TreeSet<Integer> b) {
        TreeSet<Integer> r = new TreeSet<>(a);
        r.addAll(b);
        return r;
    }

    private static TreeSet<Integer> intersect(TreeSet<Integer> a, TreeSet<Integer> b) {
        TreeSet<Integer> r = new TreeSet<>(a);
        r.retainAll(b);
        return r;
    }

    private static TreeSet<Integer> difference(TreeSet<Integer> a, TreeSet<Integer> b) {
        TreeSet<Integer> r = new TreeSet<>(a);
        r.removeAll(b);
        return r;
    }

    private static int check(String op, TreeSet<Integer> expected, IntegerSet actual, int round) {
        List<Integer> got = new ArrayList<>();
        for (int value : actual.values()) {
            got.add(value);
        }
        if (!new ArrayList<>(expected).equals(got) || actual.size() != expected.size()) {
            LOG.severe("" + op + " mismatch at round " + round + " expected=" + expected + " got=" + got);
            return 1;
        }
        return 0;
    }
}
