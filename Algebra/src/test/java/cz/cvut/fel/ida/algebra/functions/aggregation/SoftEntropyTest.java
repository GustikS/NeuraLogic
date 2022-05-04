package cz.cvut.fel.ida.algebra.functions.aggregation;

import cz.cvut.fel.ida.algebra.functions.error.SoftEntropy;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

class SoftEntropyTest {

    @TestAnnotations.Fast
    public void normal() {
        VectorValue in = new VectorValue(
                new double[]{
                        -0.3, 0.7, -0.4, 0.1
                }
        );

        VectorValue target = new VectorValue(new double[]{0, 1, 0, 0});

        Value val = new SoftEntropy().evaluate(in, target);
        System.out.println(val);
        Value grad = new SoftEntropy().differentiate(in, target);
        System.out.println(grad);
    }

    @TestAnnotations.Fast
    public void overflow() {
        VectorValue in = new VectorValue(
                new double[]{
                        19056.858719905307, 4960.272544490949, 11971.046875371912,
                        5381.27145922252, 12381.873594408318, 4820.585859773736
                }
        );

        VectorValue target = new VectorValue(new double[]{0, 0, 0, 0, 0, 1});

        Value val = new SoftEntropy().evaluate(in, target);
        System.out.println(val);
        Value grad = new SoftEntropy().differentiate(in, target);
        System.out.println(grad);
    }

    @TestAnnotations.Fast
    public void scalar() {
        ScalarValue in = new ScalarValue(10);
        ScalarValue target = new ScalarValue(0);

        Value val = new SoftEntropy().evaluate(in, target);
        System.out.println(val.toDetailedString());
        Value grad = new SoftEntropy().differentiate(in, target);
        System.out.println(grad.toDetailedString());
    }
}