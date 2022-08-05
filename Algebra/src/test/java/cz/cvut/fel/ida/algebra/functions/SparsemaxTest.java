package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.combination.Sparsemax;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SparsemaxTest {

    @TestAnnotations.Fast
    public void basic() {
        double[] values = {0.1, 0.5, 0.7, -0.1, 0.2, 0, 0.35, -2};
        List<Value> vals = Arrays.stream(values).mapToObj(ScalarValue::new).collect(Collectors.toList());
        double[] probabilities = new Sparsemax().getProbabilities(vals);

        double[] exp_output = new double[]{0., 0.3125, 0.5125, 0., 0.0125, 0., 0.1625, 0.};
        for (int i = 0; i < exp_output.length; i++) {
            assertEquals(probabilities[i], exp_output[i], 0.0000000001);
        }
    }

    @TestAnnotations.Fast
    public void zeros() {
        double[] values = {0, 0, 0};
        List<Value> vals = Arrays.stream(values).mapToObj(ScalarValue::new).collect(Collectors.toList());
        double[] probabilities = new Sparsemax().getProbabilities(vals);

        double[] exp_output = new double[]{0.3333333, 0.3333333, 0.3333333};
        for (int i = 0; i < exp_output.length; i++) {
            assertEquals(probabilities[i], exp_output[i], 0.00001);
        }
    }


    public double[] getProbabilitiesOld(List<Value> inputs) {
        double[] z_values = new double[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            double exp = ((ScalarValue) inputs.get(i)).value;
            z_values[i] = exp;
        }
        double[] z_sorted = Arrays.copyOf(z_values, z_values.length);
        Arrays.sort(z_sorted);
        int length = z_sorted.length;
        double[] k_array = new double[length];
        for (int i = 0; i < length; i++) {
            k_array[i] = z_sorted[length - 1 - i] * i + 1;
        }
        double sum = 0;
        double[] cumsum = new double[length];
        for (int i = 0; i < length; i++) {
            sum += z_sorted[z_sorted.length - 1 - i];
            cumsum[i] = sum - z_sorted[length - 1 - i];
        }
        int k_max = -1;
        for (int i = 0; i < length; i++) {
            if (k_array[i] > cumsum[i]) {
                k_max = i + 1;
            }
        }
        double sparse_sum = cumsum[k_max];

        double threshold = (sparse_sum - 1) / k_max;
        double[] out = new double[length];
        for (int i = 0; i < length; i++) {
            double val = z_values[i] - threshold;
            out[i] = val > 0 ? val : 0;
        }
        return out;
    }

    public double[] getProbabilities2(List<Value> inputs) {
        double[] z_values = new double[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            double exp = ((ScalarValue) inputs.get(i)).value;
            z_values[i] = exp;
        }
        double[] z_sorted = Arrays.copyOf(z_values, z_values.length);
        Arrays.sort(z_sorted);
        int length = z_sorted.length;
        double[] k_array = new double[length];
        for (int i = 0; i < length; i++) {
            k_array[i] = z_sorted[length - 1 - i] * i + 1;
        }
        double sum = 0;
        double[] cumsum = new double[length];
        for (int i = 0; i < length; i++) {
            sum += z_sorted[z_sorted.length - 1 - i];
            cumsum[i] = sum - z_sorted[length - 1 - i];
        }
        int k_max = -1;
        double sparse_sum = 0;
        for (int i = 0; i < length; i++) {
            if (k_array[i] > cumsum[i]) {
                k_max = i + 1;
                sparse_sum += z_sorted[length - 1 - i];
            }
        }

        double threshold = (sparse_sum - 1) / k_max;
        double[] out = new double[length];
        for (int i = 0; i < length; i++) {
            double val = z_values[i] - threshold;
            out[i] = val > 0 ? val : 0;
        }
        return out;
    }
}