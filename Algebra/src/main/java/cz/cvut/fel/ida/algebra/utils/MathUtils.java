package cz.cvut.fel.ida.algebra.utils;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class MathUtils {
    private static final Logger LOG = Logger.getLogger(MathUtils.class.getName());

    public static Value getMeanValue(List<Value> values) {
        Value mean = values.get(0).getForm();
        for (Value val : values) {
            mean.incrementBy(val);
        }
        Value times = mean.times(new ScalarValue(1.0 / values.size()));
        return times;
    }

    public static Value getStd(List<Value> values, Value mean) {
        Value var = mean.getForm();
        for (Value val : values) {
            var.incrementBy(val.minus(mean).apply(x -> x * x));
        }
        Value times = var.times(new ScalarValue(1.0 / (values.size() - 1)));    //sample std
        return times.apply(Math::sqrt);
    }

    public static Double getMean(List<Double> values){
        Double accMean = values.stream().reduce(Double::sum).get() / values.size();
        return accMean;
    }

    public static Double getStd(List<Double> values, Double mean){
        Double accStd = values.stream().map(x -> Math.pow((x - mean), 2)).reduce(Double::sum).get() / values.size();
        return accStd;
    }
}
