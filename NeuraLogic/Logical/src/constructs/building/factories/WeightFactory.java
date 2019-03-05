package constructs.building.factories;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by gusta on 5.3.18.
 */
public class WeightFactory {
    private static final Logger LOG = Logger.getLogger(WeightFactory.class.getName());

    private int index = 0;

    /**
     * Prefix for an unknown weight
     */
    public String genericName = "w";

    private Map<String, Weight> str2weight;
    private Map<Weight, Weight> weight2weight;

    public WeightFactory() {
        str2weight = new HashMap<>();
        weight2weight = new HashMap<>();
    }

    public WeightFactory(Collection<Weight> weights) {
        str2weight = weights.stream().collect(Collectors.toMap(Weight::toString, Function.identity()));
        weight2weight = weights.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
    }

    public Weight construct(String from) {
        Weight result = str2weight.get(from);
        if (result == null) {
            result = new Weight(index++, from, null, false);
            str2weight.put(from, result);
            weight2weight.put(result, result);
        }
        return result;
    }

    public Weight construct(Weight from) {
        Weight result = weight2weight.get(from);
        if (result != null) {
            str2weight.put(result.toString(), result);
            weight2weight.put(result, result);
        }
        return from;
    }

    public Weight construct(String name, Value value, boolean fixed) {
        if (value == null){
            return null;
        }
        Weight result = str2weight.get(name);
        if (result == null) {
            result = new Weight(index++, name, value, fixed);
            str2weight.put(name, result);
            weight2weight.put(result, result);
        }
        return result;
    }

    /**
     * Generic unknown weights are not put into cache.
     * @param value
     * @param fixed
     * @return
     */
    public Weight construct(Value value, boolean fixed) {
        if (value == null){
            return null;
        }
        Weight result = new Weight(index, genericName + index++, value, fixed);
        //str2weight.put(genericName, result);
        //weight2weight.put(result, result);

        return result;
    }

    public Weight mergeWeights(Aggregation aggregationFcn, Weight a, Weight b) {
        if ((a.isShared || b.isShared) && !a.equals(b)) {
            LOG.severe("Trying to merge two different shared weights" + a + " != " + b);
        }
        return new Weight(index++, a.name + b.name, aggregationFcn.evaluate(Arrays.asList(a.value, b.value)), a.isFixed && b.isFixed);
    }
}