package cz.cvut.fel.ida.algebra.functions.aggregation;

import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.Exponentiation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExponentiationTest {

    @TestAnnotations.Fast
    public void basic() {
        double[] values = {0.1, 0.5, 0.7, -0.1, 0.2, 0, 0.35, -2};
        Exponentiation exponentiation = new Exponentiation();
        Value exps = exponentiation.evaluate(new VectorValue(values));

        System.out.println(exps);

        for (int i = 0; i < values.length; i++) {
            assertEquals(exps.get(i), Math.exp(values[i]), 0.00001);
        }
    }

}