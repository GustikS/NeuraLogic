package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

/**
 * Element-wise product of all the values.
 * Can be applied to a List of Values, or to a single Value producing a ScalarValue as a multiplication of all the contained scalars.
 *
 * But it is not an Aggregation, since the gradient is not a single Value.
 */
public class ElementProduct extends Product implements Transformation {
    private static final Logger LOG = Logger.getLogger(ElementProduct.class.getName());

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public Combination replaceWithSingleton() {
        return Combination.Singletons.elementProduct;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value mult = inputs.get(0).clone();
        for (int i = 1; i < inputs.size(); i++) {
            try {
                mult.elementMultiplyBy(inputs.get(i));
            } catch (ArithmeticException e){
                mult = mult.elementTimes(inputs.get(i));
            }
        }
        return mult;
    }

    @Override
    public Value evaluate(Value combinedInputs) {
        double product = getProduct(combinedInputs);
        return new ScalarValue(product);
    }

    /**
     * Inefficient, can remember the product... see the TransformationState
     *
     * @param combinedInputs
     * @return
     */
    @Override
    public Value differentiate(Value combinedInputs) {
        double product = getProduct(combinedInputs);
        return getGradient(combinedInputs, product);
    }

    public static double getProduct(Value combinedInputs) {
        double product = 1;
        for (Double element : combinedInputs) {
            product *= element;
        }
        return product;
    }

    public static Value getGradient(Value combinedInputs, double product) {
        Value form = combinedInputs.getForm();
        int i = 0;
        for (Double element : combinedInputs) {
            form.set(i++, product / element);
        }
        return form;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        if (singleInput)
            return new TransformationState(Combination.Singletons.elementProduct);
        else
            return new AggregationState(Combination.Singletons.elementProduct);
    }


    public static class AggregationState extends InputArrayState {

        Value combinedInputs;

        public AggregationState(Combination combination) {
            super(combination);
        }

        @Override
        public Value evaluate() {
            combinedInputs = Combination.Singletons.elementProduct.evaluate(accumulatedInputs);
            return combinedInputs;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            combinedInputs = null;
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient;
        }

        @Override
        public Value nextInputGradient() {
//            Value gradient = combinedInputs.transposedView().elementDivideBy(accumulatedInputs.get(i++)); // check if transposition needed??
            Value gradient = combinedInputs.elementDivideBy(accumulatedInputs.get(i++));
            gradient.elementMultiplyBy(processedGradient);
            return gradient;
        }
    }

    public static class TransformationState extends Transformation.State {

        double product;

        public TransformationState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            product = 1;
        }

        @Override
        public Value evaluate() {
            product = getProduct(input);
            return new ScalarValue(product);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            Value gradient = getGradient(input, product);
            gradient.elementMultiplyBy(topGradient);
            processedGradient = gradient;
        }

    }
}
