package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Cosine similarity between 2 vectors
 * - a very special activation (Combination) designed for 2 inputs
 */
public class CosineSim implements Combination {
    private static final Logger LOG = Logger.getLogger(CosineSim.class.getName());

    @Override
    public Combination replaceWithSingleton() {
        return Singletons.cosineSim;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        if (inputs.size() == 2) {
            Value x = inputs.get(0);
            Value y = inputs.get(1);
            if (!(x instanceof VectorValue) || !(y instanceof VectorValue)) {
                LOG.severe("Can only calculate cosine similarity between vectors!");
            }
            return new ScalarValue(cosine(x, y));

        } else {
            LOG.severe("Cannot calculate cosine similarity from more than 2 inputs");
            return null;
        }
    }

    @NotNull
    public double cosine(Value x, Value y) {

        Iterator<Double> i1 = x.iterator();
        Iterator<Double> i2 = y.iterator();
        double dots = 0;
        double lenX = 0;
        double lenY = 0;
        while (i1.hasNext() && i2.hasNext()) {
            double a = i1.next();
            double b = i2.next();
            dots += a * b;
            lenX += a * a;
            lenY += b * b;
        }
        return dots / (Math.sqrt(lenX) * Math.sqrt(lenY));
    }

    @Override
    public boolean isPermutationInvariant() {
        return false;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(Singletons.cosineSim);
    }

    public static class State extends Combination.InputArrayState {

        List<Value> inputGradients;

        public State(Combination combination) {
            super(combination);
        }

        @Override
        public void setupDimensions(Value value) {
            super.setupDimensions(value);
            inputGradients = new ArrayList<>(2);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            inputGradients.clear();
        }

        @Override
        public Value evaluate() {
            return Singletons.cosineSim.evaluate(accumulatedInputs);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient;
            inputGradients = differentiate(accumulatedInputs);
        }

        @Override
        public Value nextInputGradient() {
            return processedGradient.elementTimes(inputGradients.get(i++));
        }


        public List<Value> differentiate(List<Value> inputs) {
            if (inputs.size() == 2) {
                Value x = inputs.get(0);
                Value y = inputs.get(1);


                Iterator<Double> i1 = x.iterator();
                Iterator<Double> i2 = y.iterator();
                double dots = 0;
                double lenX = 0;
                double lenY = 0;
                int count = 0;
                while (i1.hasNext() && i2.hasNext()) {
                    double a = i1.next();
                    double b = i2.next();
                    dots += a * b;
                    lenX += a * a;
                    lenY += b * b;
                    count++;
                }
                double lens = Math.sqrt(lenX) * Math.sqrt(lenY);
                double cosine = dots / (lens);

                Value diffX = x.getForm();
                Value diffY = y.getForm();


                for (int i = 0; i < count; i++) {
                    double a = y.get(i) / lens - cosine * x.get(i) / lenX;
                    double b = x.get(i) / lens - cosine * y.get(i) / lenY;
                    diffX.set(i, a);
                    diffY.set(i, b);
                }

                inputGradients.clear();
                inputGradients.add(diffX);
                inputGradients.add(diffY);
                return inputGradients;

            } else {
                LOG.severe("Cannot calculate cosine similarity from more than 2 inputs");
                return null;
            }
        }
    }
}
