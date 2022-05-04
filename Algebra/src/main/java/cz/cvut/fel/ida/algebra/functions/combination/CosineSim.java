package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is somewhere between activation and Aggregation...
 * todo test softmax as an aggregation fcn
 */
public class CosineSim extends Combination {
    private static final Logger LOG = Logger.getLogger(CosineSim.class.getName());


    @Override
    public Aggregation replaceWithSingleton() {
        return Singletons.cosineSim;
    }

    public Value evaluate(Value combinedInputs) {
        LOG.severe("Cannot calculate cosine similarity from a single value");
        return null;
    }

    public Value differentiate(Value summedInputs) {
        LOG.severe("Cannot calculate cosine similarity derivative from a single value");
        return null;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        if (inputs.size() == 2) {
            Value x = inputs.get(0);
            Value y = inputs.get(1);
            if (!(x instanceof VectorValue)) {
                LOG.severe("Can only calculate cosine similarity between vectors!");
            }
            return new ScalarValue(cosine(x, y));

        } else {
            LOG.severe("Cannot calculate cosine similarity from more than 2 inputs");
            return null;
        }
    }


    @Override
    public Value differentiate(List<Value> inputs) {
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

            LOG.severe("Cosine sim derivate not implemented yet");      //todo now  - need to pass two values here instead of one - create a corresponding AggregationState
            return null;

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
    public boolean isInputSymmetric() {
        return false;
    }

    @Override
    public boolean isComplex() {
        return true;
    }
}
