package constructs.building.factories;

import constructs.WeightedPredicate;
import ida.utils.tuples.Pair;
import networks.structure.Weight;

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
    }

    public WeightedPredicateFactory(Collection<WeightedPredicate> preds) {
        str2pred = preds.stream().collect(Collectors.toMap(WeightedPredicate::toString, Function.identity()));
        pred2pred = preds.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
    }

    public WeightedPredicate construct(String from, int arity, Boolean special) {
        WeightedPredicate result = str2pred.get(from + "/" + arity);
        if (result == null) {
            result = WeightedPredicate.construct(from, arity, special);
            str2pred.put(from + "/" + arity, result);
            pred2pred.put(result, result);
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
            pred2pred.get(predicateWeightPair.r).offset = predicateWeightPair.s;
            changes++;
        }
        return changes;
    }

    public WeightedPredicate construct(String name, int arity) {
        return construct(name, arity, null);
    }
}
