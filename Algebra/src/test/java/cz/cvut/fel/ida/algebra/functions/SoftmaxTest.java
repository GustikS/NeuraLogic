package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SoftmaxTest {

    @TestAnnotations.Fast
    public void basic() {
        double[] values = {0.1, 0.5, 0.7, -0.1, 0.2, 0, 0.35, -2};
        List<Value> vals = Arrays.stream(values).mapToObj(ScalarValue::new).collect(Collectors.toList());
        double[] probabilities = new Softmax().getProbabilities(vals);

        double[] exp_output = new double[]{0.1169705, 0.17449947, 0.21313414, 0.09576734, 0.12927239, 0.10583928, 0.15019309, 0.01432379};
        for (int i = 0; i < exp_output.length; i++) {
            assertEquals(probabilities[i], exp_output[i], 0.00001);
        }
    }
}