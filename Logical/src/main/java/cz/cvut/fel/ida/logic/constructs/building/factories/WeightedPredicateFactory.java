package cz.cvut.fel.ida.logic.constructs.building.factories;

import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.constructs.WeightedPredicate;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * To keep track of created predicates in some local scope (for predicate sharing)
 * Created by gusta on 5.3.18.
 */
public class WeightedPredicateFactory {
    private static final Logger LOG = Logger.getLogger(WeightedPredicateFactory.class.getName());

    private Map<String, WeightedPredicate> str2pred;
    private Map<WeightedPredicate, WeightedPredicate> pred2pred;

    public WeightedPredicateFactory() {
        str2pred = new HashMap<>();
        pred2pred = new HashMap<>();
    }

    public WeightedPredicateFactory(Collection<WeightedPredicate> preds) {
        str2pred = preds.stream().collect(Collectors.toMap(WeightedPredicate::toString, Function.identity()));
        pred2pred = preds.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
    }

    public WeightedPredicate construct(String from, int arity, Boolean special, Boolean hidden) {
        WeightedPredicate result = str2pred.get(from + "/" + arity);
        if (result == null) {
            result = WeightedPredicate.construct(from, arity, special, hidden);
            if (!hidden) {  // store it for reuse only if it is not hidden!
                str2pred.put(from + "/" + arity, result);
                pred2pred.put(result, result);
            }
        } else {
            if (special && !result.predicate.special) {     // a correction if a user states the predicate as special only in some places -> the predicate is made globally special
                result.predicate.special = true;
            }
            if (hidden && !result.predicate.hidden){    // BUT predicates can be used differently as hidden (e.g. negated) in different places
                return WeightedPredicate.construct(from, arity, special, hidden);   // hidden predicates are not shared, since they will go away anyway...
            }
        }
        return result;
    }

    public WeightedPredicate construct(WeightedPredicate from) {
        if (pred2pred.containsKey(from)) {
            return pred2pred.get(from);
        } else {
            str2pred.put(from.predicate.name + "/" + from.predicate.arity, from);
            pred2pred.put(from, from);
            return from;
        }
    }

    public int addOffsets(List<Pair<WeightedPredicate, Weight>> predicateOffsetsList) {
        int changes = 0;
        for (Pair<WeightedPredicate, Weight> predicateWeightPair : predicateOffsetsList) {
            pred2pred.get(predicateWeightPair.r).weight = predicateWeightPair.s;
            changes++;
        }
        return changes;
    }

    public WeightedPredicate construct(String name, int arity) {
        return construct(name, arity, name.startsWith("@"), name.startsWith("_"));
    }
}
