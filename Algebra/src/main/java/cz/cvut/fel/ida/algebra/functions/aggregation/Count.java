package cz.cvut.fel.ida.algebra.functions.aggregation;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Count implements Aggregation {

    private static final Logger LOG = Logger.getLogger(Count.class.getName());

    public Count replaceWithSingleton() {
        return Singletons.count;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        return new ScalarValue(inputs.size());
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        LOG.warning("Propagating gradient through a COUNT fcn");
        return Value.ZERO;    //todo check
    }

    /**
     * Returns SIZE of the input tensor (length for a vector, number of cells for a matrix)
     */
    public Value evaluate(Value combinedInputs) {
        int len = 1;
        for (int i : combinedInputs.size()) {
            len *= i;
        }
        return new ScalarValue(len);
    }

    public Value differentiate(Value summedInputs) {
        LOG.warning("Propagating gradient through a SIZE fcn");
        return Value.ZERO;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        if (singleInput)
            return new TransformationState(Singletons.count);
        else
            return new AggregationState(Singletons.count);
    }


    public static class AggregationState extends Combination.State {
        int currentIndex = 0;

        public AggregationState(Combination combination) {
            super(combination);
        }

        @Override
        public void cumulate(Value value) {
            currentIndex++;
        }

        @Override
        public void setupDimensions(Value value) {
            currentIndex = 0;
        }

        @Override
        public void invalidate() {
            currentIndex = 0;
        }

        @Override
        public Value evaluate() {
            return new ScalarValue(currentIndex);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            //void
        }

        @Override
        public Value nextInputGradient() {
            return Value.ZERO;  //here we really want to stop the gradient flow!
        }
    }

    public static class TransformationState extends Transformation.State {

        public TransformationState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = Value.ZERO;     //here we really want to stop the gradient flow!
        }
    }
}
