package cz.cvut.fel.ida.algebra.functions.aggregation;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Average implements Aggregation {

    private static final Logger LOG = Logger.getLogger(Average.class.getName());

    public Average replaceWithSingleton() {
        return Singletons.average;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value sum = inputs.get(0).clone();
        for (int i = 1, len = inputs.size(); i < len; i++) {
            try {
                sum.incrementBy(inputs.get(i));
            } catch (ArithmeticException e){
                sum = sum.plus(inputs.get(i));      // in case we e.g. start with a ScalarValue and the next one is a VectorValue, the broadcasting wouldn't work
            }
        }

        sum.elementMultiplyBy(new ScalarValue(1.0 / inputs.size()));
        return sum;
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        return new ScalarValue(1.0 / inputs.size());
    }

    @Override
    public Value evaluate(Value combinedInputs) {
        double sum = 0;
        int count = 0;
        for (Double summedInput : combinedInputs) {
            sum += summedInput;
            count++;
        }
        return new ScalarValue(sum/count);
    }

    @Override
    public Value differentiate(Value combinedInputs) {
        int len = 1;
        for (int i : combinedInputs.size()) {
            len *= i;
        }
        return new ScalarValue(1/len);
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        if (singleInput)
            return new TransformationState(Singletons.average);
        else
            return new AggregationState(Singletons.average);
    }

    public static class AggregationState extends Aggregation.State {
        int count = 0;  //these are to stay the same after init!
        ScalarValue inverseCount;   //these are to stay the same after init!

        public AggregationState(Combination combination) {
            super(combination);
        }


        @Override
        public void cumulate(Value value) {
            combinedInputs.incrementBy(value);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient.elementTimes(inverseCount);
        }

        @Override
        public Value evaluate() {
//            return sum.apply(x -> x / count);
            return combinedInputs.times(inverseCount);
        }

        @Override
        public Value initEval(List<Value> values) {
            count = values.size();
            inverseCount = new ScalarValue(1.0 / count);
            return super.initEval(values);
        }

        @Override
        public void invalidate() {
            combinedInputs.zero();
        }

        @Override
        public Value nextInputGradient() {
            return processedGradient;
        }
    }

    /**
     * We could reuse the count here, but it's probably not worth the rewriting...
     */
    public static class TransformationState extends Transformation.State {

        public TransformationState(Transformation transformation) {
            super(transformation);
        }
    }

}
