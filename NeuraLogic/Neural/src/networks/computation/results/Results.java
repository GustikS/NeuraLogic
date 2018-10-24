package networks.computation.results;

import networks.computation.training.evaluation.values.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Results {
    List<Result> outputs = new ArrayList<>();

    public abstract String toString();

    public void addResult(Value target, Value output) {
        outputs.add(new Result(target, output));
    }

    public void addResult(Result result) {
        outputs.add(result);
    }

    public abstract boolean calculate();
}