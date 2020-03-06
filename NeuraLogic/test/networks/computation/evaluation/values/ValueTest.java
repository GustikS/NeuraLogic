package networks.computation.evaluation.values;

import evaluation.values.ScalarValue;
import evaluation.values.Value;
import evaluation.values.VectorValue;
import org.junit.Test;

import java.util.Arrays;

public class ValueTest {

    @Test
    public void doubleDispatch() {
        Value scalar = new ScalarValue(1);
        VectorValue vector = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        Value times = scalar.times(vector);
        System.out.println(times.toString());
    }
}