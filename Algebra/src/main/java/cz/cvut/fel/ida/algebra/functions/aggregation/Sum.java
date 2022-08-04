package cz.cvut.fel.ida.algebra.functions.aggregation;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Sum implements Aggregation {

    private static final Logger LOG = Logger.getLogger(Sum.class.getName());

    public Sum replaceWithSingleton() {
        return Singletons.sum;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value sum = inputs.get(0).clone();
        for (int i = 1, len = inputs.size(); i < len; i++) {
            sum.incrementBy(inputs.get(i));
        }
        return sum;
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        return Value.ONE;
    }


    @Override
    public Value evaluate(Value combinedInputs) {
        double sum = 0;
        for (Double summedInput : combinedInputs) {
            sum += summedInput;
        }
        return new ScalarValue(sum);
    }

    @Override
    public Value differentiate(Value combinedInputs) {
        return Value.ONE;
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }


    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        if (singleInput)
            return new TransformationState(Singletons.sum);
        else
            return new AggregationState(Singletons.sum);
    }



    public static class AggregationState extends Aggregation.State {

        public AggregationState(Combination combination) {
            super(combination);
        }

        @Override
        public void cumulate(Value value) {
            combinedInputs.incrementBy(value);
        }

        @Override
        public Value evaluate() {
            return combinedInputs;
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient;
        }

        @Override
        public void invalidate() {
            combinedInputs.zero();
        }

        @Override
        public Value nextInputDerivative() {
            return processedGradient;
        }
    }

    public static class TransformationState extends Transformation.State {

        public TransformationState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient;
        }
    }
}