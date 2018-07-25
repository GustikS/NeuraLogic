package constructs.building.factories;

import networks.evaluation.values.Value;
import networks.structure.Weight;

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
            result = Weight.construct(from);
            str2weight.put(from, result);
            weight2weight.put(result, result);
        }
        return result;
    }

    public Weight construct(Weight from) {
        Weight result = weight2weight.get(from);
        if (result == null){
            str2weight.put(result.toString(), result);
            weight2weight.put(result, result);
        }
        return from;
    }

    public Weight construct(String name, Value value, boolean fixed) {
        Weight result = str2weight.get(from);
        if (result == null) {
            result = Weight.construct(from);
            str2weight.put(from, result);
            weight2weight.put(result, result);
        }
        return result;
    }
}
