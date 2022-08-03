package cz.cvut.fel.ida.algebra.functions.aggregation;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.states.Pooling;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Average extends Aggregation {

    private static final Logger LOG = Logger.getLogger(Average.class.getName());

    public Average replaceWithSingleton() {
        return Singletons.average;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value sum = inputs.get(0).clone();
        for (int i = 1, len = inputs.size(); i < len; i++) {
            sum.incrementBy(inputs.get(i));
        }
        return sum.times(new ScalarValue(1.0 / inputs.size()));
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        return new ScalarValue(1.0 / inputs.size());    //todo check
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }


    public static class State extends Aggregation.State {
        int count = 0;
        ScalarValue inverseCount;

        public Avg(Value initSum) {
            combinedInputs = initSum;
        }

        @Override
        public void cumulate(Value value) {
            combinedInputs.incrementBy(value);
            count++;
        }

        @Override
        public Value evaluate() {
//            return sum.apply(x -> x / count);
            return combinedInputs.times(inverseCount);
        }

        @Override
        public Value gradient() {
            return inverseCount;
        }

        @Override
        public void invalidate() {
            combinedInputs.zero();
            inverseCount = new ScalarValue(1.0 / count);
            count = 0;    //it will be the same every time but anyway...
        }

        @Override
        public Value nextInputDerivative() {
            return inverseCount;
        }
    }

}
