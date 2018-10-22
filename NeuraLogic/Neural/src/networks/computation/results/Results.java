package networks.computation.results;

import ida.utils.tuples.Pair;
import networks.computation.training.evaluation.values.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Results {
    List<Pair<Value, Value>> outputs = new ArrayList<>();

    public abstract String toString();

    public void addResult(Value target, Value output) {
        outputs.add(new Pair<>(target, output));
    }

    public abstract boolean calculate();
}