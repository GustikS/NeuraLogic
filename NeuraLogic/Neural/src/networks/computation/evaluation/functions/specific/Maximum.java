package networks.computation.evaluation.functions.specific;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.AggregationState;

import java.util.List;
import java.util.logging.Logger;

public class Maximum extends Aggregation {
    private static final Logger LOG = Logger.getLogger(Maximum.class.getName());

    @Override
    public String getName() {
        return "MAX";
    }

    @Override
    public Maximum replaceWithSingleton() {
        return Singletons.maximum;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value max = inputs.get(0);
        for (int i = 1; i < inputs.size(); i++) {
            Value value;
            if ((value = inputs.get(i)).greaterThan(max)) {
                max = value;
            }
        }
        return max;
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        return new ScalarValue(1);  //todo check
    }

    @Override
    public AggregationState getAggregationState() {
        return new AggregationState.Pooling.Max(this);
    }

}
