package evaluation.functions.specific;

import evaluation.functions.Aggregation;
import evaluation.values.ScalarValue;
import evaluation.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Maximum extends Aggregation {
    private static final Logger LOG = Logger.getLogger(Maximum.class.getName());

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
    public boolean isInputSymmetric() {
        return true;
    }


}
